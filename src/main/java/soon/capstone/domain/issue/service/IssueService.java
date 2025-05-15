package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.issue.entity.Issue;
import soon.capstone.domain.issue.entity.IssueStatus;
import soon.capstone.domain.issue.repository.issue.IssueRepository;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;
import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.global.exception.common.UnauthorizedException;
import soon.capstone.infrastructure.github.service.dto.GithubIssueCreateServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueDetailListServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueDetailServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueUpdateServiceRequest;
import soon.capstone.infrastructure.github.service.dto.response.GithubIssueDetailResponse;
import soon.capstone.infrastructure.github.service.issue.GithubIssueService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final GithubIssueService githubIssueService;
    private final AssigneeService assigneeService;
    private final IssueLabelRelationService issueLabelRelationService;

    @Caching(evict = {
        @CacheEvict(value = "issuesWithOrganization", key = "#organizationName + ':' + #repositoryName"),
        @CacheEvict(value = "issuesWithRepository", key = "#organizationName + ':' + #repositoryName"),
        @CacheEvict(value = "issueDetail", key = "#result + ':' + #organizationName + ':' + #repositoryName")
    })
    public Long create(
        Long memberId,
        String organizationName,
        String repositoryName,
        String title,
        String content,
        String assignees,
        List<String> labels,
        Milestone milestone,
        Project project,
        TeamMember teamMember
    ) {
        validateAssignee(memberId, organizationName, repositoryName, assignees);

        Long githubIssueNumber = createGithubIssue(
            memberId,
            organizationName,
            repositoryName,
            title,
            content,
            assignees,
            labels
        );

        Issue newIssue = Issue.createNewIssue(title, content, githubIssueNumber, teamMember, milestone, project);
        Long savedIssueNumber = issueRepository.save(newIssue);

        issueLabelRelationService.linkIssueWithLabels(newIssue, labels, memberId, organizationName, repositoryName, project);

        return savedIssueNumber;
    }

    @Caching(evict = {
        @CacheEvict(value = "issuesWithOrganization", key = "#organizationName + ':' + #repositoryName"),
        @CacheEvict(value = "issuesWithRepository", key = "#organizationName + ':' + #repositoryName"),
        @CacheEvict(value = "issueDetail", key = "#issueId + ':' + #organizationName + ':' + #repositoryName")
    })
    @Transactional
    public void update(
        Long memberId,
        Long issueId,
        String organizationName,
        String repositoryName,
        String title,
        String content,
        List<String> labels,
        String assignees,
        String state,
        TeamMember teamMember,
        Milestone milestone
    ) {
        validateAssignee(memberId, organizationName, repositoryName, assignees);

        Issue issue = issueRepository.findById(issueId);
        updateGithubIssue(memberId,
            issue,
            organizationName,
            repositoryName,
            title,
            content,
            labels,
            assignees,
            state
        );

        issue.updateIssue(title, content, state, milestone, teamMember);
        issueLabelRelationService.updateIssueRelation(issue, labels, milestone.getProject());
    }

    @Caching(evict = {
        @CacheEvict(value = "issuesWithOrganization", key = "#organizationName + ':' + #repositoryName"),
        @CacheEvict(value = "issuesWithRepository", key = "#organizationName + ':' + #repositoryName"),
        @CacheEvict(value = "issueDetail", key = "#issueId + ':' + #organizationName + ':' + #repositoryName")
    })
    @Transactional
    public void closedIssue(Long memberId, Long issueId, String organizationName, String repositoryName) {
        Issue issue = issueRepository.findById(issueId);
        issue.closed();
        updateGithubIssueStatus(memberId, issue, organizationName, repositoryName, IssueStatus.CLOSED);
    }

    @Caching(evict = {
        @CacheEvict(value = "issuesWithOrganization", key = "#organizationName + ':' + #repositoryName"),
        @CacheEvict(value = "issuesWithRepository", key = "#organizationName + ':' + #repositoryName"),
        @CacheEvict(value = "issueDetail", key = "#issueId + ':' + #organizationName + ':' + #repositoryName")
    })
    @Transactional
    public void reopenIssue(Long memberId, Long issueId, String organizationName, String repositoryName) {
        Issue issue = issueRepository.findById(issueId);
        issue.reopen();
        updateGithubIssueStatus(memberId, issue, organizationName, repositoryName, IssueStatus.OPEN); // TODO: 차후 interface로 closed와 통일
    }

    @Cacheable(value = "issueDetail", key = "#issueId + ':' + #organizationName + ':' + #repositoryName")
    public IssueDetailResponse getIssueDetail(Long memberId, Long issueId, String organizationName, String repositoryName) {
        Issue issue = issueRepository.findById(issueId);
        GithubIssueDetailResponse githubResponse = getGithubIssueDetail(memberId, issue.getGithubIssueNumber(), organizationName, repositoryName);
        List<IssueLabelDetailResponse> labels = issueLabelRelationService.findByLabelsByIssueId(issue);

        return IssueDetailResponse.builder()
            .issueId(issue.getId())
            .title(githubResponse.title())
            .content(githubResponse.body())
            .creator(githubResponse.getCreator())
            .status(githubResponse.state())
            .labels(labels)
            .build();
    }

    @Cacheable(value = "issuesWithRepository", key = "#organizationName + ':' + #repositoryName")
    public List<IssueDetailResponse> getIssuesWithRepository(Long memberId, String organizationName, String repositoryName) {
        List<GithubIssueDetailResponse> githubResponses = getGithubIssueDetails(memberId, organizationName, repositoryName, true);
        return toIssueDetailResponses(githubResponses);
    }

    @Cacheable(value = "issuesWithOrganization", key = "#organizationName + ':' + #repositoryName")
    public List<IssueDetailResponse> getIssuesWithOrganization(Long memberId, String organizationName, String repositoryName) {
        List<GithubIssueDetailResponse> githubResponses = getGithubIssueDetails(memberId, organizationName, repositoryName, false);
        return toIssueDetailResponses(githubResponses);
    }

    private List<GithubIssueDetailResponse> getGithubIssueDetails(Long memberId, String organizationName, String repositoryName, boolean byRepository) {
        GithubIssueDetailListServiceRequest request = GithubIssueDetailListServiceRequest.builder()
            .memberId(memberId)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .build();

        List<GithubIssueDetailResponse> responses = byRepository
            ? githubIssueService.getIssuesWithRepository(request)
            : githubIssueService.getIssuesWithOrganization(request);

        return responses.stream()
            .filter(GithubIssueDetailResponse::isPureIssue)
            .toList();
    }

    private List<IssueDetailResponse> toIssueDetailResponses(List<GithubIssueDetailResponse> githubResponses) {
        List<Long> issueNumbers = githubResponses.stream()
            .map(GithubIssueDetailResponse::number)
            .toList();

        Map<Long, Issue> issueMap = issueRepository.findByGithubIssueNumberIn(issueNumbers).stream()
            .collect(Collectors.toMap(Issue::getGithubIssueNumber, Function.identity()));

        return githubResponses.stream()
            .map(response -> toIssueDetailResponse(response, issueMap.get(response.number())))
            .toList();
    }

    private IssueDetailResponse toIssueDetailResponse(GithubIssueDetailResponse githubIssue, Issue issue) {
        List<IssueLabelDetailResponse> labels = issue != null
            ? issueLabelRelationService.findByLabelsByIssueId(issue)
            : List.of();

        return IssueDetailResponse.builder()
            .issueId(issue != null ? issue.getId() : null)
            .title(githubIssue.title())
            .content(githubIssue.body())
            .creator(githubIssue.getCreator())
            .status(githubIssue.state())
            .labels(labels)
            .build();
    }

    private void validateAssignee(Long memberId, String organizationName, String repositoryName, String assignees) {
        if (!assigneeService.isAssigneeValid(memberId, organizationName, repositoryName, assignees)) {
            log.warn("유효하지 않은 담당자: memberId={}, assignee={}", memberId, assignees);
            throw new UnauthorizedException();
        }
    }

    private Long createGithubIssue(
        Long memberId,
        String organizationName,
        String repositoryName,
        String title,
        String content,
        String assignees,
        List<String> labels
    ) {
        GithubIssueCreateServiceRequest request = GithubIssueCreateServiceRequest.builder()
            .memberId(memberId)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .title(title)
            .body(content)
            .assignees(assignees)
            .labels(labels)
            .build();
        return githubIssueService.createGithubIssue(request);
    }

    private void updateGithubIssue(
        Long memberId,
        Issue issue,
        String organizationName,
        String repositoryName,
        String title,
        String content,
        List<String> labels,
        String assignees,
        String state
    ) {
        GithubIssueUpdateServiceRequest request = GithubIssueUpdateServiceRequest.builder()
            .memberId(memberId)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .issueNumber(issue.getGithubIssueNumber())
            .title(title)
            .body(content)
            .labels(labels)
            .assignees(assignees)
            .state(state)
            .build();
        githubIssueService.updateGithubIssue(request);
    }

    private void updateGithubIssueStatus(
        Long memberId,
        Issue issue,
        String organizationName,
        String repositoryName,
        IssueStatus status
    ) {
        GithubIssueUpdateServiceRequest request = GithubIssueUpdateServiceRequest.builder()
            .memberId(memberId)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .issueNumber(issue.getGithubIssueNumber())
            .state(status.name())
            .build();
        githubIssueService.updateGithubIssue(request);
    }

    private GithubIssueDetailResponse getGithubIssueDetail(
        Long memberId,
        Long issueNumber,
        String organizationName,
        String repositoryName
    ) {
        GithubIssueDetailServiceRequest request = GithubIssueDetailServiceRequest.builder()
            .memberId(memberId)
            .issueNumber(issueNumber)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .build();
        return githubIssueService.getIssueDetail(request);
    }

}
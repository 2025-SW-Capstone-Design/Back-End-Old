package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import soon.capstone.infrastructure.github.service.dto.GithubIssueDetailServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueUpdateServiceRequest;
import soon.capstone.infrastructure.github.service.dto.response.GithubIssueDetailResponse;
import soon.capstone.infrastructure.github.service.issue.GithubIssueService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final GithubIssueService githubIssueService;
    private final AssigneeService assigneeService;
    private final IssueLabelRelationService issueLabelRelationService;

    @CacheEvict(value = "issueDetail", key = "#result + #organizationName + #repositoryName")
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

        issueLabelRelationService.linkIssueWithLabels(newIssue, labels, memberId, organizationName, repositoryName);

        return savedIssueNumber;
    }

    @CacheEvict(value = "issueDetail", key = "#issueId + #organizationName + #repositoryName")
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

        updateGithubIssue(
            memberId,
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
        issueLabelRelationService.updateIssueRelation(issue, labels);
    }

    @CacheEvict(value = "issueDetail", key = "#issueId + #organizationName + #repositoryName")
    @Transactional
    public void closedIssue(
        Long memberId,
        Long issueId,
        String organizationName,
        String repositoryName
    ) {
        Issue issue = issueRepository.findById(issueId);
        issue.closed();

        closedGithubIssue(memberId, issue, organizationName, repositoryName);
    }

    @Cacheable(value = "issueDetail", key = "#issueId + #organizationName + #repositoryName")
    public IssueDetailResponse getIssueDetail(
        Long memberId,
        Long issueId,
        String organizationName,
        String repositoryName
    ) {
        Issue issue = issueRepository.findById(issueId);

        GithubIssueDetailResponse githubResponse = getGithubIssueDetail(
            memberId,
            issue.getGithubIssueNumber(),
            organizationName,
            repositoryName
        );
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

    private void validateAssignee(
        Long memberId,
        String organizationName,
        String repositoryName,
        String assignees
    ) {
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

    private void closedGithubIssue(
        Long memberId,
        Issue issue,
        String organizationName,
        String repositoryName
    ) {
        GithubIssueUpdateServiceRequest request = GithubIssueUpdateServiceRequest.builder()
            .memberId(memberId)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .issueNumber(issue.getGithubIssueNumber())
            .state(IssueStatus.CLOSED.name())
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
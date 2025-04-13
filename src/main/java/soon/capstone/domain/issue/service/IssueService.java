package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.issue.entity.Issue;
import soon.capstone.domain.issue.repository.issue.IssueRepository;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.global.exception.common.UnauthorizedException;
import soon.capstone.infrastructure.github.service.dto.GithubIssueCreateServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueUpdateServiceRequest;
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

    @Transactional
    public void update(
        Long memberId,
        Issue issue,
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

}
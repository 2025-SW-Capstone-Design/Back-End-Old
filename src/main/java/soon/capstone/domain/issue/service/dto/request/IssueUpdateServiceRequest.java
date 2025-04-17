package soon.capstone.domain.issue.service.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record IssueUpdateServiceRequest(

    Long memberId,
    Long teamId,
    Long issueId,
    String organizationName,
    String repositoryName,
    String title,
    String content,
    List<String> labels,
    String assignees,
    String state,
    Long milestoneId

) {
}
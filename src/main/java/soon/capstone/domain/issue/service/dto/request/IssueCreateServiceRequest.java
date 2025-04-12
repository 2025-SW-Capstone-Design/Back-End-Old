package soon.capstone.domain.issue.service.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record IssueCreateServiceRequest(

    Long teamId,
    Long projectId,
    Long milestoneId,
    Long memberId,
    String organizationName,
    String repositoryName,
    String title,
    String content,
    String assignees,
    List<String> labels

) {
}
package soon.capstone.domain.issue.service.dto.request;

import lombok.Builder;

@Builder
public record IssueTemplateCreateServiceRequest(

    String title,
    String description,
    String content,
    String type,
    Long projectId,
    Long teamId

) {
}
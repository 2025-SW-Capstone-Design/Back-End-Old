package soon.capstone.domain.issue.service.dto.request;

import lombok.Builder;

@Builder
public record IssueTemplateUpdateServiceRequest(

    Long issueTemplateId,
    String title,
    String description,
    String content,
    String type,
    Long projectId,
    Long teamId

) {

}
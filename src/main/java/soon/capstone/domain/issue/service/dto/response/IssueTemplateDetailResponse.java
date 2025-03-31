package soon.capstone.domain.issue.service.dto.response;

import lombok.Builder;
import soon.capstone.domain.issue.entity.IssueTemplate;

@Builder
public record IssueTemplateDetailResponse(

    Long id,
    String title,
    String description,
    String content,
    String type

) {

    public static IssueTemplateDetailResponse of(IssueTemplate issueTemplate) {
        return IssueTemplateDetailResponse.builder()
            .id(issueTemplate.getId())
            .title(issueTemplate.getTitle())
            .description(issueTemplate.getDescription())
            .content(issueTemplate.getContent())
            .type(issueTemplate.getType().name())
            .build();
    }

}
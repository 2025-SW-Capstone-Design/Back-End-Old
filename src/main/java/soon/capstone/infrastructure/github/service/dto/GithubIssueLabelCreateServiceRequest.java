package soon.capstone.infrastructure.github.service.dto;

import lombok.Builder;
import soon.capstone.infrastructure.github.dto.request.GithubIssueLabelCreateRequest;

@Builder
public record GithubIssueLabelCreateServiceRequest(

    Long memberId,
    String title,
    String description,
    String color,
    String organizationName,
    String repositoryName

) {

    public GithubIssueLabelCreateRequest toGithubRequest() {
        return GithubIssueLabelCreateRequest.builder()
            .name(title)
            .color(color)
            .description(description)
            .build();
    }

}
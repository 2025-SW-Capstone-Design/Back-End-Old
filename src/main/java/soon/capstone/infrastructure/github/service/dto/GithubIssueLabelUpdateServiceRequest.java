package soon.capstone.infrastructure.github.service.dto;

import lombok.Builder;
import soon.capstone.infrastructure.github.dto.request.GithubIssueLabelUpdateRequest;

@Builder
public record GithubIssueLabelUpdateServiceRequest(

    Long memberId,
    String organizationName,
    String repositoryName,
    String oldTitle,
    String newTitle,
    String color,
    String description

) {

    public GithubIssueLabelUpdateRequest toGithubRequest() {
        return GithubIssueLabelUpdateRequest.builder()
            .newName(newTitle)
            .color(color)
            .description(description)
            .build();
    }

}
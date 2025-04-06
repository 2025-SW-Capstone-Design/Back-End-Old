package soon.capstone.infrastructure.github.dto;

import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;

public record GithubIssueLabelDetailDto(

    String name,
    String color,
    String description

) {

    public IssueLabelDetailResponse toResponse() {
        return IssueLabelDetailResponse.builder()
            .name(name)
            .color(color)
            .description(description)
            .build();
    }

}
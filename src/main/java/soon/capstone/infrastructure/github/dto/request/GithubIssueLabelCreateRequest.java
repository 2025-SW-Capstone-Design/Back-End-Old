package soon.capstone.infrastructure.github.dto.request;

import lombok.Builder;

@Builder
public record GithubIssueLabelCreateRequest(

    String name,
    String color,
    String description

) {
}
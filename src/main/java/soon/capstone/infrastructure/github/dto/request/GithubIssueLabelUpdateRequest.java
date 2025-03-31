package soon.capstone.infrastructure.github.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record GithubIssueLabelUpdateRequest(

    @JsonProperty("new_name")
    String newName,
    String color,
    String description

) {
}
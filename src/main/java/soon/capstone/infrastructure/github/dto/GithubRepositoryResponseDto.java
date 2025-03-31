package soon.capstone.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubRepositoryResponseDto(
        @JsonProperty("node_id")
        String nodeId
) {
}

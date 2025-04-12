package soon.capstone.infrastructure.github.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record GithubAssigneesAppendRequest(

    List<String> assignees

) {
}
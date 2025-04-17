package soon.capstone.infrastructure.github.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record GithubIssueUpdateRequest(

    String title,
    String body,
    List<String> assignees,
    String state,
    List<String> labels

) {
}
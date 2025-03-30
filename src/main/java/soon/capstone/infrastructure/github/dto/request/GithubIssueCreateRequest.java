package soon.capstone.infrastructure.github.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record GithubIssueCreateRequest(

    String title,
    String body,
    List<String> assignees,
    List<String> labels

) {
}
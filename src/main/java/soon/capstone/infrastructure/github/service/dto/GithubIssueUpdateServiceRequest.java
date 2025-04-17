package soon.capstone.infrastructure.github.service.dto;

import lombok.Builder;
import soon.capstone.infrastructure.github.dto.request.GithubIssueUpdateRequest;

import java.util.List;

@Builder
public record GithubIssueUpdateServiceRequest(

    Long memberId,
    String organizationName,
    String repositoryName,
    Long issueNumber,
    String title,
    String body,
    List<String> labels,
    String assignees,
    String state

) {

    public GithubIssueUpdateRequest toGithubRequest() {
        return GithubIssueUpdateRequest.builder()
            .title(title)
            .body(body)
            .labels(labels)
            .assignees(List.of(assignees))
            .state(state)
            .build();
    }

}
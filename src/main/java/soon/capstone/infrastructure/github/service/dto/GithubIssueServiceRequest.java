package soon.capstone.infrastructure.github.service.dto;

import lombok.Builder;
import soon.capstone.infrastructure.github.dto.request.GithubIssueCreateRequest;

import java.util.List;

@Builder
public record GithubIssueServiceRequest(

    Long memberId,
    String organizationName,
    String repositoryName,
    String title,
    String body,
    List<String> assignees,
    List<String> labels
) {

    public GithubIssueCreateRequest toGithubRequest() {
        return GithubIssueCreateRequest.builder()
            .title(title)
            .body(body)
            .assignees(assignees)
            .labels(labels)
            .build();
    }

}
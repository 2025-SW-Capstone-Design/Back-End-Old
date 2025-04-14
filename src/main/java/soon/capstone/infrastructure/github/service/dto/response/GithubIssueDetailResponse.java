package soon.capstone.infrastructure.github.service.dto.response;

import lombok.Builder;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;
import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;

import java.util.List;
import java.util.Map;

@Builder
public record GithubIssueDetailResponse(

    String title,
    String body,
    Map<String, Object> assignee,
    String state,
    List<IssueLabelDetailResponse> labels

) {

    public IssueDetailResponse toIssueDetailResponse() {
        return IssueDetailResponse.builder()
            .title(title)
            .content(body)
            .creator(assignee.get("login").toString())
            .status(state)
            .labels(labels)
            .build();
    }

}
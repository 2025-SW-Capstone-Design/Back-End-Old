package soon.capstone.infrastructure.github.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Map;

@Builder
public record GithubIssueDetailResponse(

    @JsonProperty("pull_request")
    Map<String, Object> pullRequest,

    String title,
    String body,
    Map<String, Object> assignee,
    Long number,
    String state

) {

    public String getCreator() {
        return assignee.get("login").toString();
    }

    public boolean isPureIssue() {
        return pullRequest == null;
    }

}
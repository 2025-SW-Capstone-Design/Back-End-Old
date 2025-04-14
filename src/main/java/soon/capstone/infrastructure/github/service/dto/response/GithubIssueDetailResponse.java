package soon.capstone.infrastructure.github.service.dto.response;

import lombok.Builder;

import java.util.Map;

@Builder
public record GithubIssueDetailResponse(

    String title,
    String body,
    Map<String, Object> assignee,
    String state

) {

    public String getCreator() {
        return assignee.get("login").toString();
    }

}
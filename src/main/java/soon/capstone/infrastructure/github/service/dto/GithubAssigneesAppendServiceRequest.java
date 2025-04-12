package soon.capstone.infrastructure.github.service.dto;

import lombok.Builder;
import soon.capstone.infrastructure.github.dto.request.GithubAssigneesAppendRequest;

import java.util.List;

@Builder
public record GithubAssigneesAppendServiceRequest(

    Long memberId,
    Long issueNumber,
    String organizationName,
    String repositoryName,
    String assignee

) {

    public GithubAssigneesAppendRequest toGithubRequest() {
        return GithubAssigneesAppendRequest.builder()
            .assignees(List.of(assignee))
            .build();
    }

}
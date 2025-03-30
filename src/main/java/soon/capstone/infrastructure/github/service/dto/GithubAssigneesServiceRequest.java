package soon.capstone.infrastructure.github.service.dto;

import lombok.Builder;

@Builder
public record GithubAssigneesServiceRequest(

    String organizationName,
    String repositoryName,
    String assignee,
    Long memberId

) {
}
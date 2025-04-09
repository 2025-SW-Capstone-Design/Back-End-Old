package soon.capstone.infrastructure.github.service.dto;

import lombok.Builder;

@Builder
public record GithubIssueLabelDetailServiceRequest(

    Long memberId,
    String organizationName,
    String repositoryName

) {
}
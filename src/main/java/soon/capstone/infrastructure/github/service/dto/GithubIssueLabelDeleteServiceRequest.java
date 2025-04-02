package soon.capstone.infrastructure.github.service.dto;

import lombok.Builder;

@Builder
public record GithubIssueLabelDeleteServiceRequest(

    Long memberId,
    String organizationName,
    String repositoryName,
    String title

) {

}
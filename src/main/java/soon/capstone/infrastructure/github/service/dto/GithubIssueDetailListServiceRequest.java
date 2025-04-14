package soon.capstone.infrastructure.github.service.dto;

import lombok.Builder;

@Builder
public record GithubIssueDetailListServiceRequest(

    Long memberId,
    String organizationName,
    String repositoryName

) {
}
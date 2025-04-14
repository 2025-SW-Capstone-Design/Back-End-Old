package soon.capstone.infrastructure.github.service.dto;

import lombok.Builder;

@Builder
public record GithubIssueDetailServiceRequest(

    Long memberId,
    Long issueNumber,
    String organizationName,
    String repositoryName

) {
}
package soon.capstone.infrastructure.github.service.dto;

import lombok.Builder;
import soon.capstone.infrastructure.github.dto.request.GithubIssueLabelAppendRequest;

import java.util.List;

@Builder
public record GithubIssueLabelAppendServiceRequest(

    Long memberId,
    String organizationName,
    String repositoryName,
    Long issueNumber,
    List<String> labels

) {

    public GithubIssueLabelAppendRequest toGithubRequest() {
        return GithubIssueLabelAppendRequest.builder()
            .labels(labels)
            .build();
    }

}
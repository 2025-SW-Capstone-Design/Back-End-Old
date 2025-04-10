package soon.capstone.domain.readme.service.dto.request;

import lombok.Builder;

@Builder
public record ReadmeUpdateServiceRequest(

    Long readmeId,
    String title,
    String content,
    Long memberId,
    Long projectId,
    Long teamId

) {
}
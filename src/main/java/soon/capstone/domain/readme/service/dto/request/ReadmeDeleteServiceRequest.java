package soon.capstone.domain.readme.service.dto.request;

import lombok.Builder;

@Builder
public record ReadmeDeleteServiceRequest(

    Long readmeId,
    Long memberId,
    Long teamId

) {
}
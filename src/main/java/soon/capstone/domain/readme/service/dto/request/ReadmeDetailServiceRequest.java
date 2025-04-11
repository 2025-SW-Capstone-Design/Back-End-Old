package soon.capstone.domain.readme.service.dto.request;

import lombok.Builder;

@Builder
public record ReadmeDetailServiceRequest(

    Long readmeId,
    Long memberId,
    Long teamId

) {
}
package soon.capstone.domain.readme.service.dto.request;

import lombok.Builder;

@Builder
public record ReadmeRollbackServiceRequest(

    Long readmeId,
    Long memberId,
    Long teamId,
    Long projectId

) {
}
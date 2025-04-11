package soon.capstone.domain.readme.service.dto.request;

import lombok.Builder;

@Builder
public record ReadmesListServiceRequest(

    Long projectId,
    Long memberId,
    Long teamId

) {
}
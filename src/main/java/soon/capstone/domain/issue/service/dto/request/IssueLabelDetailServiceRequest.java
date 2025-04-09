package soon.capstone.domain.issue.service.dto.request;

import lombok.Builder;

@Builder
public record IssueLabelDetailServiceRequest(

    Long teamId,
    Long projectId

) {
}
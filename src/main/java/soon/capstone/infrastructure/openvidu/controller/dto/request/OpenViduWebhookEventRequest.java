package soon.capstone.infrastructure.openvidu.controller.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

import java.time.LocalDateTime;

@Builder
public record OpenViduWebhookEventRequest(

    @NotBlank(message = "body는 필수입니다.")
    String body,

    @NotNull(message = "reservedAt은 필수입니다.")
    @Future(message = "reservedAt은 현재 시간보다 미래여야 합니다.")
    LocalDateTime reservedAt

) {

    public OpenViduWebhookEventServiceRequest toServiceRequest(Long memberId, Long teamId, String openViduToken) {
        return OpenViduWebhookEventServiceRequest.builder()
            .body(body)
            .reservedAt(reservedAt)
            .openViduToken(openViduToken)
            .memberId(memberId)
            .teamId(teamId)
            .build();
    }

}
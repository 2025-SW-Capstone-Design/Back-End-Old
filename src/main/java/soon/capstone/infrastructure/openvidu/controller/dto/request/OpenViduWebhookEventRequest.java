package soon.capstone.infrastructure.openvidu.controller.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

import java.time.LocalDateTime;

@Builder
public record OpenViduWebhookEventRequest(

    @NotNull
    JsonNode body,

    @NotNull(message = "reservedAt은 필수입니다.")
    @Future(message = "reservedAt은 현재 시간보다 미래여야 합니다.")
    LocalDateTime reservedAt,

    Long teamId

) {

    public OpenViduWebhookEventServiceRequest toServiceRequest(Long memberId, String openViduToken) {
        return OpenViduWebhookEventServiceRequest.builder()
            .body(body.toString())
            .reservedAt(reservedAt)
            .openViduToken(openViduToken)
            .memberId(memberId)
            .teamId(teamId)
            .build();
    }

}
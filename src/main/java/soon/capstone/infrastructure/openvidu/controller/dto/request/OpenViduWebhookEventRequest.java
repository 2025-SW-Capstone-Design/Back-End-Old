package soon.capstone.infrastructure.openvidu.controller.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

import java.time.LocalDateTime;

@Builder
public record OpenViduWebhookEventRequest(

    @NotNull
    JsonNode body,

    LocalDateTime reservedAt,

    Long teamId,

    Long memberId

) {

    public OpenViduWebhookEventServiceRequest toServiceRequest(String openViduToken) {
        return OpenViduWebhookEventServiceRequest.builder()
            .body(body.toString())
            .reservedAt(reservedAt)
            .openViduToken(openViduToken)
            .memberId(memberId)
            .teamId(teamId)
            .build();
    }

}
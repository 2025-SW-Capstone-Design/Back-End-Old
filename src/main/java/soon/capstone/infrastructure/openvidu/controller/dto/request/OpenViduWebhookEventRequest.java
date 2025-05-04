package soon.capstone.infrastructure.openvidu.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

import java.time.LocalDateTime;

@Builder
public record OpenViduWebhookEventRequest(

    @NotBlank(message = "body는 필수입니다.")
    String body,

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
package soon.capstone.infrastructure.openvidu.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

@Builder
public record OpenViduWebhookEventRequest(

    @NotBlank(message = "body는 필수입니다.")
    String body

) {

    public OpenViduWebhookEventServiceRequest toServiceRequest(Long memberId, Long teamId, String openViduToken) {
        return OpenViduWebhookEventServiceRequest.builder()
            .body(body)
            .openViduToken(openViduToken)
            .memberId(memberId)
            .teamId(teamId)
            .build();
    }

}
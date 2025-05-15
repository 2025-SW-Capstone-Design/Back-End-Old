package soon.capstone.infrastructure.openvidu.controller.dto.request;

import lombok.Builder;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

@Builder
public record OpenViduWebhookEventRequest(
) {

    public static OpenViduWebhookEventServiceRequest toServiceRequest(String body, String openViduToken) {
        return OpenViduWebhookEventServiceRequest.builder()
            .body(body)
            .openViduToken(openViduToken)
            .build();
    }

}
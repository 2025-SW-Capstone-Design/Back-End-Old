package soon.capstone.infrastructure.openvidu.controller.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

@Builder
public record OpenViduWebhookEventRequest(
) {

    public static OpenViduWebhookEventServiceRequest toServiceRequest(JsonNode body, String openViduToken) {
        return OpenViduWebhookEventServiceRequest.builder()
            .body(body.toString())
            .openViduToken(openViduToken)
            .build();
    }

}
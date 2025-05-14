package soon.capstone.infrastructure.openvidu.controller.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

@Builder
public record OpenViduWebhookEventRequest(

    @NotNull
    JsonNode body

) {

    public static OpenViduWebhookEventServiceRequest toServiceRequest(JsonNode body, String openViduToken) {
        return OpenViduWebhookEventServiceRequest.builder()
            .body(body.toString())
            .openViduToken(openViduToken)
            .build();
    }

}
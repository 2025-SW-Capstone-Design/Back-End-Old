package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook.WebhookEvent;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

public interface OpenViduWebhookEventHandler {

    boolean support(String eventType);

    Long handle(WebhookEvent event, OpenViduWebhookEventServiceRequest request);

}
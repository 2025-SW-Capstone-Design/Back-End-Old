package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook.WebhookEvent;

public interface OpenViduWebhookEventHandler {

    boolean support(String eventType);

    void handle(WebhookEvent event, Long teamId);

}
package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

@Slf4j
@Component
public class RoomFinishedEventHandler implements OpenViduWebhookEventHandler {

    @Override
    public boolean support(String eventType) {
        return "room_finished".equals(eventType);
    }

    @Override
    public Long handle(LivekitWebhook.WebhookEvent event, OpenViduWebhookEventServiceRequest request) {
        log.info("회의 종료: {}", event.getRoom().getName());

        return null;
    }

}
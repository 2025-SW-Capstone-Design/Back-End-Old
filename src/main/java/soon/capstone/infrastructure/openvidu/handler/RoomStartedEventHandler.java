package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook.WebhookEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoomStartedEventHandler implements OpenViduWebhookEventHandler {

    @Override
    public boolean support(String eventType) {
        return "room_started".equals(eventType);
    }

    @Override
    public void handle(WebhookEvent event, Long teamId) {
        log.info("방 생성 {}", event.getRoom().getName());
    }

}
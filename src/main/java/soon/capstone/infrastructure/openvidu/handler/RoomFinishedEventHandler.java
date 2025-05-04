package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoomFinishedEventHandler implements OpenViduWebhookEventHandler {

    @Override
    public boolean support(String eventType) {
        return "room_finished".equals(eventType);
    }

    @Override
    public void handle(LivekitWebhook.WebhookEvent event, Long teamId) {
        log.info("회의 종료: {}", event.getRoom().getName());
    }

}
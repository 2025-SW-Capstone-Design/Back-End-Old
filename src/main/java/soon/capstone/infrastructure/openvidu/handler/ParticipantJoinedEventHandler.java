package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

@Slf4j
@Component
public class ParticipantJoinedEventHandler implements OpenViduWebhookEventHandler {

    @Override
    public boolean support(String eventType) {
        return "participant_joined".equals(eventType);
    }

    @Override
    public void handle(LivekitWebhook.WebhookEvent event, OpenViduWebhookEventServiceRequest request) {
        log.info("참가자 입장: {}", event.getParticipant().getIdentity());
    }

}
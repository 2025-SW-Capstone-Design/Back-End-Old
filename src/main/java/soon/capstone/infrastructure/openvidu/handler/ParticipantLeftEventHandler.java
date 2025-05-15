package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook.WebhookEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static soon.capstone.infrastructure.openvidu.common.OpenViduEventType.PARTICIPANT_LEFT;

@Slf4j
@Component
public class ParticipantLeftEventHandler implements OpenViduWebhookEventHandler {

    @Override
    public boolean support(String eventType) {
        return PARTICIPANT_LEFT.equals(eventType);
    }

    @Override
    public void handle(WebhookEvent event, Long teamId, Long memberId) {
        log.info("참가자 퇴장: {}, 팀: {} 채팅방: {}",
            event.getParticipant().getIdentity(),
            teamId,
            event.getRoom().getName()
        );
    }

}
package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook.WebhookEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static soon.capstone.infrastructure.openvidu.common.OpenViduEventType.TRACK_UNPUBLISHED;

@Slf4j
@Component
public class TrackUnpublishedEventHandler implements OpenViduWebhookEventHandler {

    @Override
    public boolean support(String eventType) {
        return TRACK_UNPUBLISHED.equals(eventType);
    }

    @Override
    public void handle(WebhookEvent event, Long teamId, Long memberId) {
        log.info("트랙이 중단되었습니다 - 방 이름: {}, 팀: {}, 참가자: {}, 트랙({}) SID: {}",
            event.getRoom().getName(),
            teamId,
            memberId,
            event.getTrack().getType(),
            event.getTrack().getSid()
        );
    }

}
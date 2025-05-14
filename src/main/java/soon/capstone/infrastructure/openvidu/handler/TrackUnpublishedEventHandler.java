package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

@Slf4j
@RequiredArgsConstructor
@Component
public class TrackUnpublishedEventHandler implements OpenViduWebhookEventHandler {

    @Override
    public boolean support(String eventType) {
        return "track_unpublished".equals(eventType);
    }

    @Override
    public void handle(LivekitWebhook.WebhookEvent event, OpenViduWebhookEventServiceRequest request) {
        log.info("트랙이 중단되었습니다 - 방 이름: {}, 참가자: {}, 트랙 SID: {}",
            event.getRoom().getName(),
            event.getParticipant().getIdentity(),
            event.getTrack().getSid()
        );
    }

}
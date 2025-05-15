package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook.WebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import soon.capstone.domain.chatroom.service.ChatRoomService;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomFinishServiceRequest;
import soon.capstone.infrastructure.redis.openvidu.repository.TemporaryRoomIdentityRepository;

import static soon.capstone.infrastructure.openvidu.common.OpenViduEventType.ROOM_FINISHED;

@Slf4j
@RequiredArgsConstructor
@Component
public class RoomFinishedEventHandler implements OpenViduWebhookEventHandler {

    private final ChatRoomService chatRoomService;
    private final TemporaryRoomIdentityRepository temporaryRoomIdentityRepository;

    @Override
    public boolean support(String eventType) {
        return ROOM_FINISHED.equals(eventType);
    }

    @Override
    public void handle(WebhookEvent event, Long teamId, Long memberId) {
        log.info("팀: {}, 회의 종료: {}", teamId, event.getRoom().getName());

        // TODO: 회의록 최종 요약 요청
        chatRoomService.finishRoom(createChatRoomFinishServiceRequest(event, teamId, memberId));

        temporaryRoomIdentityRepository.deleteById(event.getRoom().getSid());
    }

    private ChatRoomFinishServiceRequest createChatRoomFinishServiceRequest(WebhookEvent event, Long teamId, Long memberId) {
        return ChatRoomFinishServiceRequest.builder()
            .teamId(teamId)
            .memberId(memberId)
            .sid(event.getRoom().getSid())
            .build();
    }

}
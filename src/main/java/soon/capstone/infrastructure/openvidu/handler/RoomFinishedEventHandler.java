package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import soon.capstone.domain.chatroom.service.ChatRoomService;
import soon.capstone.infrastructure.openvidu.mapper.OpenViduWebhookEventMapper;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

@Slf4j
@RequiredArgsConstructor
@Component
public class RoomFinishedEventHandler implements OpenViduWebhookEventHandler {

    private final ChatRoomService chatRoomService;

    @Override
    public boolean support(String eventType) {
        return "room_finished".equals(eventType);
    }

    @Override
    public Long handle(LivekitWebhook.WebhookEvent event, OpenViduWebhookEventServiceRequest request) {
        log.info("회의 종료: {}", event.getRoom().getName());

        // TODO: 회의록 요약 요청
        return chatRoomService.finishRoom(OpenViduWebhookEventMapper.toChatRoomFinishServiceRequest(event, request));
    }

}
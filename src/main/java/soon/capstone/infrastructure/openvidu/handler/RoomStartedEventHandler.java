package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook.WebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import soon.capstone.domain.chatroom.service.ChatRoomService;
import soon.capstone.infrastructure.openvidu.mapper.OpenViduWebhookEventMapper;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

@Slf4j
@RequiredArgsConstructor
@Component
public class RoomStartedEventHandler implements OpenViduWebhookEventHandler {

    private final ChatRoomService chatRoomService;

    @Override
    public boolean support(String eventType) {
        return "room_started".equals(eventType);
    }

    @Override
    public void handle(WebhookEvent event, OpenViduWebhookEventServiceRequest request) {
        Long roomId = chatRoomService.createRoom(OpenViduWebhookEventMapper.toChatRoomServiceRequest(event, request));
        log.info("방 생성{}: {}", roomId, event.getRoom().getName());
    }

}
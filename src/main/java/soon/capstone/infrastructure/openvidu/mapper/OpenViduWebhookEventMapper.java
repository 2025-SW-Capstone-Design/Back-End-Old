package soon.capstone.infrastructure.openvidu.mapper;

import livekit.LivekitWebhook.WebhookEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomCreateServiceRequest;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomFinishServiceRequest;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OpenViduWebhookEventMapper {

    public static ChatRoomCreateServiceRequest toChatRoomServiceRequest(WebhookEvent event, OpenViduWebhookEventServiceRequest request) {
        return ChatRoomCreateServiceRequest.builder()
            .sid(event.getRoom().getSid())
            .title(event.getRoom().getName())
            .build();
    }

    public static ChatRoomFinishServiceRequest toChatRoomFinishServiceRequest(WebhookEvent event, OpenViduWebhookEventServiceRequest request) {
        return ChatRoomFinishServiceRequest.builder()
            .sid(event.getRoom().getSid())
            .build();
    }

}
package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook.WebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import soon.capstone.domain.chatroom.service.ChatRoomService;
import soon.capstone.domain.chatroom.service.ChatRoomTeamMemberService;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomAddMemberServiceRequest;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomCreateServiceRequest;

import static soon.capstone.infrastructure.openvidu.common.OpenViduEventType.PARTICIPANT_JOINED;

@Slf4j
@RequiredArgsConstructor
@Component
public class ParticipantJoinedEventHandler implements OpenViduWebhookEventHandler {

    private final ChatRoomService chatRoomService;
    private final ChatRoomTeamMemberService chatRoomTeamMemberService;

    @Override
    public boolean support(String eventType) {
        return PARTICIPANT_JOINED.equals(eventType);
    }

    @Override
    public void handle(WebhookEvent event, Long teamId, Long memberId) {
        log.info("참가자 입장: {}, 팀: {}, 채팅방: {}",
            memberId,
            teamId,
            event.getRoom().getName()
        );

        ChatRoomCreateServiceRequest request = createChatroomCreateServiceRequest(event, teamId, memberId);
        Long chatRoomId = chatRoomService.createRoom(request);

        ChatRoomAddMemberServiceRequest addMemberRequest = createChatRoomAddMemberServiceRequest(chatRoomId, teamId, memberId);
        chatRoomTeamMemberService.addMemberToChatRoom(addMemberRequest);
    }

    private ChatRoomCreateServiceRequest createChatroomCreateServiceRequest(WebhookEvent event, Long teamId, Long memberId) {
        return ChatRoomCreateServiceRequest.builder()
            .title(event.getRoom().getName())
            .sid(event.getRoom().getSid())
            .teamId(teamId)
            .memberId(memberId)
            .build();
    }

    private ChatRoomAddMemberServiceRequest createChatRoomAddMemberServiceRequest(Long chatRoomId, Long teamId, Long memberId) {
        return ChatRoomAddMemberServiceRequest.builder()
            .chatRoomId(chatRoomId)
            .teamId(teamId)
            .memberId(memberId)
            .build();
    }

}
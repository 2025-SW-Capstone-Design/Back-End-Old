package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook.WebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import soon.capstone.domain.chatroom.service.ChatRoomService;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomFinishServiceRequest;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomSummarizeServiceRequest;
import soon.capstone.infrastructure.redis.openvidu.repository.TemporaryRoomIdentityRepository;
import soon.capstone.infrastructure.redis.summary.service.SummaryTextService;

import java.util.stream.Collectors;

import static soon.capstone.infrastructure.openvidu.common.OpenViduEventType.ROOM_FINISHED;

@Slf4j
@RequiredArgsConstructor
@Component
public class RoomFinishedEventHandler implements OpenViduWebhookEventHandler {

    private final ChatRoomService chatRoomService;
    private final SummaryTextService summaryTextService;
    private final TemporaryRoomIdentityRepository temporaryRoomIdentityRepository;

    @Override
    public boolean support(String eventType) {
        return ROOM_FINISHED.equals(eventType);
    }

    @Override
    public void handle(WebhookEvent event, Long teamId, Long memberId) {
        log.info("팀: {}, 회의 종료: {}", teamId, event.getRoom().getName());

        Long chatRoomId = chatRoomService.finishRoom(createChatRoomFinishServiceRequest(event, teamId, memberId));
        String fileName = event.getRoom().getName() + memberId + teamId + ".ogg";
        chatRoomService.summarizeChatroomToS3File(createChatRoomSummarizeServiceRequest(teamId, memberId, chatRoomId, fileName));
//        String combinedSummary = summaryTextService.findAllByChatRoomId(chatRoomId).stream()
//            .map(summaryText -> summaryText.getSummary() + "\n")
//            .collect(Collectors.joining());

//        chatRoomService.summarizeChatroom(createChatRoomSummarizeServiceRequest(teamId, memberId, chatRoomId, combinedSummary));
//        summaryTextService.resetIndex(chatRoomId);

        temporaryRoomIdentityRepository.deleteById(event.getRoom().getSid());
    }

    private ChatRoomFinishServiceRequest createChatRoomFinishServiceRequest(WebhookEvent event, Long teamId, Long memberId) {
        return ChatRoomFinishServiceRequest.builder()
            .teamId(teamId)
            .memberId(memberId)
            .sid(event.getRoom().getSid())
            .build();
    }

    private ChatRoomSummarizeServiceRequest createChatRoomSummarizeServiceRequest(Long teamId, Long memberId, Long chatRoomId, String combinedSummary) {
        return ChatRoomSummarizeServiceRequest.builder()
            .teamId(teamId)
            .memberId(memberId)
            .chatRoomId(chatRoomId)
            .text(combinedSummary)
            .isFinal(true)
            .build();
    }

}
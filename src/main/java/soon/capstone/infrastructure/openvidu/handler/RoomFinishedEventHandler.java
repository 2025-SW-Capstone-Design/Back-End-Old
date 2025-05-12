package soon.capstone.infrastructure.openvidu.handler;

import livekit.LivekitWebhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import soon.capstone.domain.chatroom.service.ChatRoomService;
import soon.capstone.infrastructure.openvidu.mapper.OpenViduWebhookEventMapper;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;
import soon.capstone.infrastructure.redis.summary.service.SummaryTextService;

import java.util.stream.Collectors;

import static soon.capstone.infrastructure.openvidu.mapper.OpenViduWebhookEventMapper.toChatRoomFinishServiceRequest;
import static soon.capstone.infrastructure.openvidu.mapper.OpenViduWebhookEventMapper.toChatRoomSummarizeServiceRequest;

@Slf4j
@RequiredArgsConstructor
@Component
public class RoomFinishedEventHandler implements OpenViduWebhookEventHandler {

    private final ChatRoomService chatRoomService;
    private final SummaryTextService summaryTextService;

    @Override
    public boolean support(String eventType) {
        return "room_finished".equals(eventType);
    }

    @Override
    public Long handle(LivekitWebhook.WebhookEvent event, OpenViduWebhookEventServiceRequest request) {
        Long chatRoomId = chatRoomService.finishRoom(toChatRoomFinishServiceRequest(event, request));
        String combinedSummary = summaryTextService.findAllByChatRoomId(chatRoomId).stream()
            .map(summaryText -> summaryText.getSummary() + "\n")
            .collect(Collectors.joining());

        chatRoomService.summarizeChatroom(toChatRoomSummarizeServiceRequest(request, combinedSummary, chatRoomId));
        summaryTextService.resetIndex(chatRoomId);

        log.info("회의 종료: {} : chatRoomId: {}", event.getRoom().getName(), chatRoomId);
        return chatRoomId;
    }

}
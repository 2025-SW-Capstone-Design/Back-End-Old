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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static soon.capstone.infrastructure.openvidu.common.OpenViduEventType.ROOM_FINISHED;

@Slf4j
@RequiredArgsConstructor
@Component
public class RoomFinishedEventHandler implements OpenViduWebhookEventHandler {

    private final ChatRoomService chatRoomService;
    private final SummaryTextService summaryTextService;
    private final TemporaryRoomIdentityRepository temporaryRoomIdentityRepository;

    private final Lock summarizationLock = new ReentrantLock();

    private final java.util.Set<String> processedRooms = ConcurrentHashMap.newKeySet();

    @Override
    public boolean support(String eventType) {
        return ROOM_FINISHED.equals(eventType);
    }

    @Override
    public void handle(WebhookEvent event, Long teamId, Long memberId) {
        String roomSid = event.getRoom().getSid();
        String roomName = event.getRoom().getName();

        log.info("팀: {}, 회의 종료 이벤트 수신: {}, SID: {}", teamId, roomName, roomSid);

        Long chatRoomId = chatRoomService.finishRoom(createChatRoomFinishServiceRequest(event, teamId, memberId));

        summarizationLock.lock();
        try {
            if (processedRooms.contains(roomSid)) {
                log.info("회의 {} (SID: {})은 이미 요약 처리되었습니다. 중복 요약 방지.", roomName, roomSid);
                return;
            }
            processedRooms.add(roomSid);
            log.info("회의 {} (SID: {}) 요약 처리 시작", roomName, roomSid);
            chatRoomService.summarizeChatroomToS3File(createChatRoomSummarizeServiceRequest(teamId, memberId, chatRoomId, roomName));

            log.info("회의 {} (SID: {}) 요약 처리 완료", roomName, roomSid);
            if (processedRooms.size() > 100) {
                processedRooms.clear();
            }
        } finally {
            summarizationLock.unlock();
        }
        temporaryRoomIdentityRepository.deleteById(roomSid);
    }

    private ChatRoomFinishServiceRequest createChatRoomFinishServiceRequest(WebhookEvent event, Long teamId, Long memberId) {
        return ChatRoomFinishServiceRequest.builder()
            .teamId(teamId)
            .memberId(memberId)
            .sid(event.getRoom().getSid())
            .build();
    }

    private ChatRoomSummarizeServiceRequest createChatRoomSummarizeServiceRequest(Long teamId, Long memberId, Long chatRoomId, String meetingName) {
        return ChatRoomSummarizeServiceRequest.builder()
            .teamId(teamId)
            .memberId(memberId)
            .chatRoomId(chatRoomId)
            .text(meetingName)
            .isFinal(true)
            .build();
    }
}

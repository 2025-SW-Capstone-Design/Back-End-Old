package soon.capstone.domain.chatroom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.chatroom.entity.ChatRoom;
import soon.capstone.domain.chatroom.repository.chatroom.ChatRoomRepository;
import soon.capstone.domain.chatroom.service.dto.request.*;
import soon.capstone.domain.chatroom.service.dto.response.ChatRoomDetailsResponse;
import soon.capstone.domain.meetinglog.service.MeetingLogService;
import soon.capstone.domain.meetinglog.service.dto.request.MeetingLogCreateServiceRequest;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.service.TeamMemberValidator;
import soon.capstone.infrastructure.openai.service.GptSummaryService;
import soon.capstone.infrastructure.redis.summary.repository.SummaryTextRepository;
import soon.capstone.infrastructure.s3.service.S3Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final GptSummaryService gptSummaryService;
    private final TeamRepository teamRepository;
    private final TeamMemberValidator teamMemberValidator;
    private final SummaryTextRepository summaryTextRepository;
    private final MeetingLogService meetingLogService;
    private final S3Service s3Service;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public Long createRoom(ChatRoomCreateServiceRequest request) {
        teamMemberValidator.validateTeamMember(request.teamId(), request.memberId());

        if (chatRoomRepository.existsByTeamIdAndSid(request.teamId(), request.sid())) {
            log.info("채팅방이 이미 존재합니다. 팀 ID: {}, SID: {}", request.teamId(), request.sid());
            ChatRoom existingChatRoom = chatRoomRepository.findByTeamIdAndSid(request.teamId(), request.sid());
            return existingChatRoom.getId();
        }

        Team team = teamRepository.findById(request.teamId());
        ChatRoom chatRoom = ChatRoom.create(request.title(), team, request.sid());

        return chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public Long finishRoom(ChatRoomFinishServiceRequest request) {
        teamMemberValidator.validateTeamMember(request.teamId(), request.memberId());

        ChatRoom chatRoom = chatRoomRepository.findBySid(request.sid());
        chatRoom.finish();
        return chatRoom.getId();
    }

    @Transactional
    public Long resumeRoom(ChatRoomResumeServiceRequest request) {
        teamMemberValidator.validateTeamMember(request.teamId(), request.memberId());

        ChatRoom chatRoom = chatRoomRepository.findById(request.chatRoomId());
        chatRoom.resume();
        return chatRoom.getId();
    }

    public List<ChatRoomDetailsResponse> getChatRoomDetails(ChatRoomDetailsServiceRequest request) {
        teamMemberValidator.validateTeamMember(request.teamId(), request.memberId());

        return chatRoomRepository.findAllByTeamId(request.teamId()).stream()
            .map(ChatRoomDetailsResponse::from)
            .toList();
    }

    public void summarizeChatroom(ChatRoomSummarizeServiceRequest request) {
        teamMemberValidator.validateTeamMember(request.teamId(), request.memberId());

        String summaryToText = gptSummaryService.summaryToText(request.text(), request.isFinal());
        if (request.isFinal()) {
            meetingLogService.create(createMeetingLogCreateServiceRequest(request.teamId(), request.memberId(), summaryToText));
        } else {
            summaryTextRepository.save(request.chatRoomId(), summaryToText);
        }
    }

    public void summarizeChatroomToS3File(ChatRoomSummarizeServiceRequest request) {
        byte[] fileBytes = s3Service.getFileBytes(bucketName, request.text());
        String s = gptSummaryService.summaryToText(fileBytes, request.text());
        meetingLogService.create(createMeetingLogCreateServiceRequest(request.teamId(), request.memberId(), s));
    }


    private MeetingLogCreateServiceRequest createMeetingLogCreateServiceRequest(Long teamId, Long memberId, String content) {
        return MeetingLogCreateServiceRequest.builder()
            .teamId(teamId)
            .memberId(memberId)
            .content(content)
            .build();
    }

}
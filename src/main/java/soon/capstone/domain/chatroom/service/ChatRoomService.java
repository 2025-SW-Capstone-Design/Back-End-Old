package soon.capstone.domain.chatroom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.chatroom.entity.ChatRoom;
import soon.capstone.domain.chatroom.repository.chatroom.ChatRoomRepository;
import soon.capstone.domain.chatroom.service.dto.request.*;
import soon.capstone.domain.chatroom.service.dto.response.ChatRoomDetailsResponse;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.service.TeamMemberValidator;
import soon.capstone.infrastructure.openai.service.GptSummaryService;
import soon.capstone.infrastructure.redis.summary.repository.SummaryTextRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomTeamMemberService chatRoomTeamMemberService;
    private final GptSummaryService gptSummaryService;
    private final TeamRepository teamRepository;
    private final TeamMemberValidator teamMemberValidator;
    private final SummaryTextRepository summaryTextRepository;

    public Long createRoom(ChatRoomCreateServiceRequest request) {
        teamMemberValidator.validateTeamMember(request.teamId(), request.memberId());

        Team team = teamRepository.findById(request.teamId());
        ChatRoom chatRoom = ChatRoom.create(request.title(), request.reservedAt(), team, request.sid());
        Long savedChatRoomId = chatRoomRepository.save(chatRoom);

        addMemberToChatRoom(request, chatRoom, team);

        return savedChatRoomId;
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
        summaryTextRepository.save(request.chatRoomId(), summaryToText);
    }

    private void addMemberToChatRoom(ChatRoomCreateServiceRequest request, ChatRoom chatRoom, Team team) {
        ChatRoomAddMemberServiceRequest addMemberRequest = ChatRoomAddMemberServiceRequest.builder()
            .chatRoomId(chatRoom.getId())
            .teamId(team.getId())
            .memberId(request.memberId())
            .build();
        chatRoomTeamMemberService.addMemberToChatRoom(addMemberRequest);
    }

}
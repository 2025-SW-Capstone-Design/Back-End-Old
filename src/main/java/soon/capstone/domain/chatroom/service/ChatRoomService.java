package soon.capstone.domain.chatroom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.chatroom.entity.ChatRoom;
import soon.capstone.domain.chatroom.repository.ChatRoomRepository;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomAddMemberServiceRequest;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomCreateServiceRequest;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomFinishServiceRequest;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomResumeServiceRequest;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.service.TeamMemberValidator;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomTeamMemberService chatRoomTeamMemberService;
    private final TeamRepository teamRepository;
    private final TeamMemberValidator teamMemberValidator;

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


    private void addMemberToChatRoom(ChatRoomCreateServiceRequest request, ChatRoom chatRoom, Team team) {
        ChatRoomAddMemberServiceRequest addMemberRequest = ChatRoomAddMemberServiceRequest.builder()
            .chatRoomId(chatRoom.getId())
            .teamId(team.getId())
            .memberId(request.memberId())
            .build();
        chatRoomTeamMemberService.addMemberToChatRoom(addMemberRequest);
    }

}
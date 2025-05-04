package soon.capstone.domain.chatroom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.chatroom.entity.ChatRoom;
import soon.capstone.domain.chatroom.repository.ChatRoomRepository;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomAddMemberServiceRequest;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomCreateServiceRequest;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomTeamMemberService chatRoomTeamMemberService;
    private final TeamRepository teamRepository;

    public Long createRoom(ChatRoomCreateServiceRequest request) {
        Team team = teamRepository.findById(request.teamId());
        ChatRoom chatRoom = ChatRoom.create(request.title(), request.reservedAt(), team, request.sid());
        Long savedChatRoomId = chatRoomRepository.save(chatRoom);

        addMemberToChatRoom(request, chatRoom, team);

        return savedChatRoomId;
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
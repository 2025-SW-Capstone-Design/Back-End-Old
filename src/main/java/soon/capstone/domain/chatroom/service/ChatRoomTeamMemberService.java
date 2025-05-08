package soon.capstone.domain.chatroom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.chatroom.entity.ChatRoom;
import soon.capstone.domain.chatroom.entity.ChatRoomTeamMember;
import soon.capstone.domain.chatroom.repository.chatroom.ChatRoomRepository;
import soon.capstone.domain.chatroom.repository.member.ChatRoomTeamMemberRepository;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomAddMemberServiceRequest;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomTeamMembersDetailServiceRequest;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomTeamMemberService {

    private final ChatRoomTeamMemberRepository chatRoomTeamMemberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ChatRoomRepository chatRoomRepository;

    public void addMemberToChatRoom(ChatRoomAddMemberServiceRequest request) {
        TeamMember teamMember = teamMemberRepository.findByTeamIdAndMemberId(request.teamId(), request.memberId());
        ChatRoom chatRoom = chatRoomRepository.findById(request.chatRoomId());

        saveToChatRoomTeamMember(chatRoom, teamMember);
    }

    public List<TeamMemberDetailResponse> getTeamMembersByChatRoom(ChatRoomTeamMembersDetailServiceRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findByIdAndTeamId(request.chatRoomId(), request.teamId());
        return chatRoomTeamMemberRepository.getTeamMembersByChatRoom(chatRoom);
    }

    private void saveToChatRoomTeamMember(ChatRoom chatRoom, TeamMember teamMember) {
        ChatRoomTeamMember chatMember = ChatRoomTeamMember.builder()
            .chatRoom(chatRoom)
            .teamMember(teamMember)
            .build();
        chatRoomTeamMemberRepository.save(chatMember);
    }

}
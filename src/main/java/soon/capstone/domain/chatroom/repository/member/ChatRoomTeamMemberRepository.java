package soon.capstone.domain.chatroom.repository.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.chatroom.entity.ChatRoom;
import soon.capstone.domain.chatroom.entity.ChatRoomTeamMember;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;
import soon.capstone.global.exception.chatroom.ChatRoomTeamMemberNotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ChatRoomTeamMemberRepository {

    private final ChatRoomTeamMemberJpaRepository chatRoomTeamMemberJpaRepository;

    public void save(ChatRoomTeamMember chatRoomTeamMember) {
        chatRoomTeamMemberJpaRepository.save(chatRoomTeamMember);
    }

    public void saveAll(List<ChatRoomTeamMember> chatRoomTeamMembers) {
        chatRoomTeamMemberJpaRepository.saveAll(chatRoomTeamMembers);
    }

    public ChatRoomTeamMember findByChatRoomIdAndTeamMemberId(Long chatRoomId, Long teamMemberId) {
        return chatRoomTeamMemberJpaRepository.findByChatRoomIdAndTeamMemberId(chatRoomId, teamMemberId)
            .orElseThrow(ChatRoomTeamMemberNotFoundException::new);
    }

    public List<TeamMemberDetailResponse> getTeamMembersByChatRoom(ChatRoom chatRoom) {
        return chatRoomTeamMemberJpaRepository.getTeamMembersByChatRoom(chatRoom);
    }

    public void deleteAllInBatch() {
        chatRoomTeamMemberJpaRepository.deleteAllInBatch();
    }

}
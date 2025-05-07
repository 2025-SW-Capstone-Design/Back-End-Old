package soon.capstone.domain.chatroom.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.chatroom.entity.ChatRoomTeamMember;
import soon.capstone.global.exception.chatroom.ChatRoomTeamMemberNotFoundException;

@RequiredArgsConstructor
@Repository
public class ChatRoomTeamMemberRepository {

    private final ChatRoomTeamMemberJpaRepository chatRoomTeamMemberJpaRepository;

    public void save(ChatRoomTeamMember chatRoomTeamMember) {
        chatRoomTeamMemberJpaRepository.save(chatRoomTeamMember);
    }

    public ChatRoomTeamMember findByChatRoomIdAndTeamMemberId(Long chatRoomId, Long teamMemberId) {
        return chatRoomTeamMemberJpaRepository.findByChatRoomIdAndTeamMemberId(chatRoomId, teamMemberId)
            .orElseThrow(ChatRoomTeamMemberNotFoundException::new);
    }

    public void deleteAllInBatch() {
        chatRoomTeamMemberJpaRepository.deleteAllInBatch();
    }

}
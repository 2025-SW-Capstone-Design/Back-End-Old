package soon.capstone.domain.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.chatroom.entity.ChatRoomTeamMember;

import java.util.Optional;

public interface ChatRoomTeamMemberJpaRepository extends JpaRepository<ChatRoomTeamMember, Long> {

    Optional<ChatRoomTeamMember> findByChatRoomIdAndTeamMemberId(Long chatRoomId, Long teamMemberId);

}
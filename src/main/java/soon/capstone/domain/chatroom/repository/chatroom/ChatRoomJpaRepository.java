package soon.capstone.domain.chatroom.repository.chatroom;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.chatroom.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findBySid(String sid);

    Optional<ChatRoom> findByIdAndTeamId(Long chatRoomId, Long teamId);

    List<ChatRoom> findAllByTeamId(Long team);

    boolean existsByTeamIdAndSid(Long teamId, String sid);

    Optional<ChatRoom> findByTeamIdAndSid(Long teamId, String sid);

}
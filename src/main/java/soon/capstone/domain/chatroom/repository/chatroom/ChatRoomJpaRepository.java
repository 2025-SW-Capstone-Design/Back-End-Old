package soon.capstone.domain.chatroom.repository.chatroom;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.chatroom.entity.ChatRoom;

import java.util.Optional;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findBySid(String sid);

}
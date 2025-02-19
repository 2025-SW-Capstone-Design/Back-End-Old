package soon.capstone.domain.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.chatroom.entity.ChatRoom;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {

}
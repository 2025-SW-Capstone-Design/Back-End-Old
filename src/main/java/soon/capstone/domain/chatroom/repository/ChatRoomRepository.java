package soon.capstone.domain.chatroom.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChatRoomRepository {

    private final ChatRoomJpaRepository chatRoomJpaRepository;

}
package soon.capstone.domain.chatroom.repository.chatroom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.chatroom.entity.ChatRoom;
import soon.capstone.global.exception.chatroom.ChatRoomNotFoundException;

@RequiredArgsConstructor
@Repository
public class ChatRoomRepository {

    private final ChatRoomJpaRepository chatRoomJpaRepository;

    public Long save(ChatRoom chatRoom) {
        return chatRoomJpaRepository.save(chatRoom).getId();
    }

    public ChatRoom findById(Long id) {
        return chatRoomJpaRepository.findById(id)
            .orElseThrow(ChatRoomNotFoundException::new);
    }

    public ChatRoom findBySid(String sid) {
        return chatRoomJpaRepository.findBySid(sid)
            .orElseThrow(ChatRoomNotFoundException::new);
    }

    public ChatRoom findByIdAndTeamId(Long chatRoomId, Long teamId) {
        return chatRoomJpaRepository.findByIdAndTeamId(chatRoomId, teamId)
            .orElseThrow(ChatRoomNotFoundException::new);
    }

    public void deleteAllInBatch() {
        chatRoomJpaRepository.deleteAllInBatch();
    }

}
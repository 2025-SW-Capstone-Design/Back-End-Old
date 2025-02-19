package soon.capstone.domain.chatroom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.chatroom.repository.ChatRoomRepository;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

}
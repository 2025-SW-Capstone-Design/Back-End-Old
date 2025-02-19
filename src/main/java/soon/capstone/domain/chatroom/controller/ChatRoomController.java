package soon.capstone.domain.chatroom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.chatroom.service.ChatRoomService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/chatroom")
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

}
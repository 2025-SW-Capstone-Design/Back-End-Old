package soon.capstone.domain.chatroom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.chatroom.controller.dto.request.ChatRoomResumeRequest;
import soon.capstone.domain.chatroom.service.ChatRoomService;
import soon.capstone.global.anootation.AuthMemberId;

@RequiredArgsConstructor
@RequestMapping("/api/v1/teams/{teamId}/chat-rooms")
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PatchMapping("/{chatRoomId}")
    public ResponseEntity<Long> resumeRoom(
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long chatRoomId
    ) {
        Long updatedChatRoomId = chatRoomService.resumeRoom(ChatRoomResumeRequest.toServiceRequest(memberId, teamId, chatRoomId));
        return ResponseEntity.ok(updatedChatRoomId);
    }

}
package soon.capstone.domain.chatroom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.chatroom.controller.dto.ChatRoomDetailsRequest;
import soon.capstone.domain.chatroom.controller.dto.request.ChatRoomResumeRequest;
import soon.capstone.domain.chatroom.service.ChatRoomService;
import soon.capstone.domain.chatroom.service.dto.response.ChatRoomDetailsResponse;
import soon.capstone.global.anootation.AuthMemberId;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<ChatRoomDetailsResponse>> getChatRoomDetails(
        @AuthMemberId Long memberId,
        @PathVariable Long teamId
    ) {
        List<ChatRoomDetailsResponse> response = chatRoomService.getChatRoomDetails(ChatRoomDetailsRequest.toServiceRequest(memberId, teamId));
        return ResponseEntity.ok(response);
    }

}
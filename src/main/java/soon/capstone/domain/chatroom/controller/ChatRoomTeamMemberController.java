package soon.capstone.domain.chatroom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.chatroom.controller.dto.ChatRoomAddMemberRequest;
import soon.capstone.domain.chatroom.service.ChatRoomTeamMemberService;
import soon.capstone.global.anootation.AuthMemberId;

@RequiredArgsConstructor
@RequestMapping("/api/v1/{teamId}/chatroom/{chatRoomId}/members")
@RestController
public class ChatRoomTeamMemberController {

    private final ChatRoomTeamMemberService chatRoomTeamMemberService;

    @PostMapping
    public ResponseEntity<Void> addTeamMemberToChatRoom(
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long chatRoomId
    ) {
        chatRoomTeamMemberService.addMemberToChatRoom(ChatRoomAddMemberRequest.toServiceRequest(memberId, teamId, chatRoomId));
        return ResponseEntity.noContent()
            .build();
    }

}
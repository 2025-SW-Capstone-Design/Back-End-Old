package soon.capstone.domain.chatroom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.chatroom.controller.dto.ChatRoomAddMemberRequest;
import soon.capstone.domain.chatroom.controller.dto.ChatRoomTeamMembersDetailRequest;
import soon.capstone.domain.chatroom.service.ChatRoomTeamMemberService;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;
import soon.capstone.global.anootation.AuthMemberId;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/teams/{teamId}/chat-rooms/{chatRoomId}/members")
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

    @GetMapping
    public ResponseEntity<List<TeamMemberDetailResponse>> getTeamMembersByChatRoom(
        @PathVariable Long teamId,
        @PathVariable Long chatRoomId
    ) {
        List<TeamMemberDetailResponse> teamMembers = chatRoomTeamMemberService.getTeamMembersByChatRoom(
            ChatRoomTeamMembersDetailRequest.toServiceRequest(teamId, chatRoomId)
        );
        return ResponseEntity.ok(teamMembers);
    }

}
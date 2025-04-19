package soon.capstone.domain.team.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.team.controller.docs.TeamControllerDocs;
import soon.capstone.domain.team.controller.dto.TeamCreateRequest;
import soon.capstone.domain.team.controller.dto.TeamInvitationRequest;
import soon.capstone.domain.team.controller.dto.TeamJoinRequest;
import soon.capstone.domain.team.service.team.TeamService;
import soon.capstone.global.anootation.AuthMemberId;

@RequiredArgsConstructor
@RequestMapping("/api/v1/teams")
@RestController
public class TeamController implements TeamControllerDocs {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<Long> createTeam(
        @Valid @RequestBody TeamCreateRequest request,
        @AuthMemberId Long memberId
    ) {
        Long teamId = teamService.createTeam(request.toServiceRequest(), memberId);

        return ResponseEntity.ok(teamId);
    }

    @PostMapping("/{teamId}/invitation-code")
    public ResponseEntity<String> generateInvitationCode(
        @AuthMemberId Long memberId,
        @PathVariable Long teamId
    ) {
        String invitationCode = teamService.generateInvitationCode(teamId, memberId);

        return ResponseEntity.ok(invitationCode);
    }

    @PostMapping("/{teamId}/invitation-emails")
    public ResponseEntity<Void> sendInvitationEmails(
        @Valid @RequestBody TeamInvitationRequest request,
        @AuthMemberId Long memberId,
        @PathVariable Long teamId
    ) {
        teamService.sendInvitationEmails(request.toServiceRequest(teamId), memberId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/join")
    public ResponseEntity<Long> joinTeam(
        @Valid @RequestBody TeamJoinRequest request,
        @AuthMemberId Long memberId
    ) {
        Long teamId = teamService.joinTeamWithInvitationCode(request.toServiceRequest(), memberId);

        return ResponseEntity.ok(teamId);
    }

}
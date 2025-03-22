package soon.capstone.domain.team.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.team.controller.dto.TeamCreateRequest;
import soon.capstone.domain.team.controller.dto.TeamGenerateInvitationCodeRequest;
import soon.capstone.domain.team.controller.dto.TeamInvitationRequest;
import soon.capstone.domain.team.service.team.TeamService;
import soon.capstone.global.anootation.AuthMemberId;

@RequiredArgsConstructor
@RequestMapping("/api/v1/teams")
@RestController
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<Long> createTeam(
        @Valid @RequestBody TeamCreateRequest request,
        @AuthMemberId Long memberId
    ) {
        Long teamId = teamService.createTeam(request.toServiceRequest(), memberId);

        return ResponseEntity.ok(teamId);
    }

    @PostMapping("/invitation-code")
    public ResponseEntity<String> generateInvitationCode(
        @Valid @RequestBody TeamGenerateInvitationCodeRequest request,
        @AuthMemberId Long memberId
    ) {
        String invitationCode = teamService.generateInvitationCode(request.toServiceRequest(), memberId);

        return ResponseEntity.ok(invitationCode);
    }

    @PostMapping("/invitation-emails")
    public ResponseEntity<Void> sendInvitationEmails(
        @Valid @RequestBody TeamInvitationRequest request,
        @AuthMemberId Long memberId
    ) {
        teamService.sendInvitationEmails(request.toServiceRequest(), memberId);

        return ResponseEntity.ok().build();
    }

}
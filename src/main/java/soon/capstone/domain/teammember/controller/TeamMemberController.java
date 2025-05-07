package soon.capstone.domain.teammember.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.teammember.controller.docs.TeamMemberControllerDocs;
import soon.capstone.domain.teammember.controller.dto.TeamMemberUpdatePositionRequest;
import soon.capstone.domain.teammember.controller.dto.TeamMemberUpdateRoleRequest;
import soon.capstone.domain.teammember.service.TeamMemberService;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;
import soon.capstone.global.anootation.AuthMemberId;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/teams/{teamId}/members")
@RestController
public class TeamMemberController implements TeamMemberControllerDocs {

    private final TeamMemberService teamMemberService;

    @GetMapping
    public ResponseEntity<List<TeamMemberDetailResponse>> getTeamMembers(
        @PathVariable("teamId") Long teamId,
        @AuthMemberId Long memberId
    ) {
        List<TeamMemberDetailResponse> teamMembers = teamMemberService.getTeamMembers(teamId, memberId);

        return ResponseEntity.ok(teamMembers);
    }

    @PatchMapping
    public ResponseEntity<Void> updateTeamMemberRole(
        @PathVariable("teamId") Long teamId,
        @Valid @RequestBody TeamMemberUpdateRoleRequest request,
        @AuthMemberId Long memberId
    ) {
        teamMemberService.updateTeamMemberRole(request.toServiceRequest(teamId, memberId));

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/position")
    public ResponseEntity<Void> updateTeamMemberPosition(
        @Valid @RequestBody TeamMemberUpdatePositionRequest request,
        @AuthMemberId Long memberId,
        @PathVariable("teamId") Long teamId
    ) {
        teamMemberService.updateTeamMemberPosition(request.toServiceRequest(teamId, memberId));

        return ResponseEntity.noContent().build();
    }

}
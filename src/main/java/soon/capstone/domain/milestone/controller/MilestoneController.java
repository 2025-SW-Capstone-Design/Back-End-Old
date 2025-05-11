package soon.capstone.domain.milestone.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.milestone.controller.docs.MilestoneControllerDocs;
import soon.capstone.domain.milestone.controller.dto.MilestoneStatusUpdateRequest;
import soon.capstone.domain.milestone.controller.dto.request.MilestoneCreateRequest;
import soon.capstone.domain.milestone.controller.dto.request.MilestoneUpdateRequest;
import soon.capstone.domain.milestone.service.MilestoneService;
import soon.capstone.domain.milestone.service.dto.response.MilestoneDetailResponse;
import soon.capstone.domain.milestone.service.dto.response.MilestoneIssueResponse;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.global.anootation.AuthMemberId;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/teams/{teamId}")
@RestController
public class MilestoneController implements MilestoneControllerDocs {

    private final MilestoneService milestoneService;

    @GetMapping
    public ResponseEntity<List<MilestoneResponse>> getMilestonesByTeam(
        @AuthMemberId Long memberId,
        @PathVariable Long teamId
    ) {
        List<MilestoneResponse> milestones = milestoneService.getMilestonesByTeam(memberId, teamId);
        return ResponseEntity.ok(milestones);
    }

    @PostMapping("/projects/{projectId}")
    public ResponseEntity<Long> createMilestone(
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long projectId,
        @RequestBody @Valid MilestoneCreateRequest milestoneCreateRequest) {
        Long milestoneId = milestoneService.createMilestone(memberId, teamId, projectId, milestoneCreateRequest.toServiceRequest());
        return ResponseEntity.ok(milestoneId);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<MilestoneResponse>> getMilestonesByProject(
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long projectId
    ) {
        List<MilestoneResponse> milestones = milestoneService.getMilestonesByProject(memberId, teamId, projectId);
        return ResponseEntity.ok(milestones);
    }

    @GetMapping("/milestones/{milestoneId}")
    public ResponseEntity<MilestoneDetailResponse> getMilestoneDetail(
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long milestoneId
    ) {
        MilestoneDetailResponse milestoneDetail = milestoneService.getMilestoneDetail(memberId, teamId, milestoneId);
        return ResponseEntity.ok(milestoneDetail);
    }

    @GetMapping("/milestones")
    public ResponseEntity<List<MilestoneIssueResponse>> getMilestoneWithIssuesDueTomorrow(
        @PathVariable Long teamId
    ) {
        List<MilestoneIssueResponse> milestoneIssues = milestoneService.getMilestoneWithIssuesDueTomorrow(teamId);
        return ResponseEntity.ok(milestoneIssues);
    }

    @PutMapping("/projects/{projectId}/milestones/{milestoneId}")
    public ResponseEntity<MilestoneResponse> updateMilestone(
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long projectId,
        @PathVariable Long milestoneId,
        @RequestBody MilestoneUpdateRequest milestoneUpdateRequest
    ) {
        MilestoneResponse milestoneResponse = milestoneService.updateMilestone(memberId, teamId, projectId, milestoneId, milestoneUpdateRequest.toServiceRequest());
        return ResponseEntity.ok(milestoneResponse);
    }

    @PatchMapping("/projects/{projectId}/milestones/{milestoneId}/status")
    public ResponseEntity<Void> updateMilestoneStatus(
        @Valid @RequestBody MilestoneStatusUpdateRequest request,
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long projectId,
        @PathVariable Long milestoneId
    ) {
        milestoneService.updateMilestoneStatus(request.toServiceRequest(memberId, teamId, projectId, milestoneId));
        return ResponseEntity.noContent().build();
    }

}
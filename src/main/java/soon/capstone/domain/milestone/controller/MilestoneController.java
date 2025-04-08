package soon.capstone.domain.milestone.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.milestone.controller.dto.request.MilestoneCreateRequest;
import soon.capstone.domain.milestone.controller.dto.response.MilestoneResponse;
import soon.capstone.domain.milestone.service.MilestoneService;
import soon.capstone.global.anootation.AuthMemberId;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/milestones")
@RestController
public class MilestoneController {

    private final MilestoneService milestoneService;

    @PostMapping
    public ResponseEntity<Long> createMilestone(@AuthMemberId Long memberId, @RequestBody @Valid MilestoneCreateRequest milestoneCreateRequest) {
        Long milestoneId = milestoneService.createMilestone(memberId, milestoneCreateRequest.toServiceRequest());
        return ResponseEntity.ok(milestoneId);
    }

    @GetMapping("/{teamId}/{projectId}")
    public ResponseEntity<List<MilestoneResponse>> getMilestonesByProject(
            @AuthMemberId Long memberId,
            @PathVariable Long teamId,
            @PathVariable Long projectId
    ) {
        List<MilestoneResponse> milestones = milestoneService.getMilestonesByProject(memberId, teamId, projectId);
        return ResponseEntity.ok(milestones);
    }

}
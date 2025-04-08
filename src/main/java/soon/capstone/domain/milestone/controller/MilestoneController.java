package soon.capstone.domain.milestone.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.milestone.controller.dto.MilestoneCreateRequest;
import soon.capstone.domain.milestone.service.MilestoneService;
import soon.capstone.global.anootation.AuthMemberId;

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

}
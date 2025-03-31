package soon.capstone.domain.issue.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.issue.controller.dto.IssueLabelCreateRequest;
import soon.capstone.domain.issue.controller.dto.IssueLabelUpdateRequest;
import soon.capstone.domain.issue.service.IssueManagementService;
import soon.capstone.global.anootation.AuthMemberId;

@RequiredArgsConstructor
@RequestMapping("/api/v1/teams/{teamId}/issue-labels")
@RestController
public class IssueLabelController {

    private final IssueManagementService issueManagementService;

    @PostMapping
    public ResponseEntity<Long> createIssueLabel(
        @Valid @RequestBody IssueLabelCreateRequest request,
        @AuthMemberId Long memberId,
        @PathVariable Long teamId
    ) {
        Long issueLabelId = issueManagementService.createIssueLabel(request.toServiceRequest(teamId), memberId);

        return ResponseEntity.ok(issueLabelId);
    }

    @PatchMapping("/{labelId}")
    public ResponseEntity<Void> updateIssueLabel(
        @Valid @RequestBody IssueLabelUpdateRequest request,
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long labelId
    ) {
        issueManagementService.updateIssueLabel(request.toServiceRequest(teamId, labelId), memberId);

        return ResponseEntity.ok().build();
    }

}
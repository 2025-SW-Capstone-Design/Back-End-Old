package soon.capstone.domain.issue.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.issue.controller.dto.IssueCreateRequest;
import soon.capstone.domain.issue.service.IssueManagementService;
import soon.capstone.global.anootation.AuthMemberId;

@RequiredArgsConstructor
@RequestMapping("/api/v1/teams/{teamId}/projects/{projectId}/issues")
@RestController
public class IssueController {

    private final IssueManagementService issueManagementService;

    @PostMapping
    public ResponseEntity<Long> createIssue(
        @Valid @RequestBody IssueCreateRequest request,
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long projectId
    ) {
        Long issueId = issueManagementService.createIssue(request.toServiceRequest(memberId, teamId, projectId));

        return ResponseEntity.ok(issueId);
    }

}
package soon.capstone.domain.issue.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.issue.controller.dto.*;
import soon.capstone.domain.issue.service.IssueManagementService;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;
import soon.capstone.global.anootation.AuthMemberId;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/teams/{teamId}")
@RestController
public class IssueController {

    private final IssueManagementService issueManagementService;

    @PostMapping("/projects/{projectId}/issues")
    public ResponseEntity<Long> createIssue(
        @Valid @RequestBody IssueCreateRequest request,
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long projectId
    ) {
        Long issueId = issueManagementService.createIssue(request.toServiceRequest(memberId, teamId, projectId));

        return ResponseEntity.ok(issueId);
    }

    @PatchMapping("/issues/{issueId}")
    public ResponseEntity<Void> updateIssue(
        @Valid @RequestBody IssueUpdateRequest request,
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long issueId
    ) {
        issueManagementService.updateIssue(request.toServiceRequest(memberId, teamId, issueId));

        return ResponseEntity.noContent()
            .build();
    }

    @PatchMapping("/issues/{issueId}/closed")
    public ResponseEntity<Void> closedIssue(
        @Valid @RequestBody IssueClosedRequest request,
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long issueId
    ) {
        issueManagementService.closedIssue(request.toServiceRequest(memberId, teamId, issueId));

        return ResponseEntity.noContent()
            .build();
    }

    @GetMapping("/projects/{projectId}/issues/{issueId}")
    public ResponseEntity<IssueDetailResponse> getIssueDetail(
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long issueId,
        @PathVariable Long projectId
    ) {
        IssueDetailResponse response = issueManagementService.getIssueDetail(
            IssueDetailRequest.toServiceRequest(memberId, teamId, issueId, projectId)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/projects/{projectId}/issues")
    public ResponseEntity<List<IssueDetailResponse>> getIssues(
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long projectId,
        @RequestParam(value = "scope") String scope
    ) {
        List<IssueDetailResponse> response = issueManagementService.getIssues(
            IssueDetailListRequest.toServiceRequest(memberId, teamId, projectId, scope)
        );

        return ResponseEntity.ok(response);
    }

}
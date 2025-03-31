package soon.capstone.domain.issue.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.issue.controller.dto.IssueTemplateCreateRequest;
import soon.capstone.domain.issue.controller.dto.IssueTemplateUpdateRequest;
import soon.capstone.domain.issue.service.IssueManagementService;
import soon.capstone.domain.issue.service.dto.response.IssueTemplateDetailResponse;
import soon.capstone.global.anootation.AuthMemberId;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/teams/{teamId}/issue-templates")
@RestController
public class IssueTemplateController {

    private final IssueManagementService issueManagementService;

    @PostMapping
    public ResponseEntity<Long> createIssueTemplate(
        @Valid @RequestBody IssueTemplateCreateRequest request,
        @PathVariable Long teamId,
        @AuthMemberId Long memberId
    ) {
        Long issueTemplateId = issueManagementService.createIssueTemplate(request.toServiceRequest(teamId), memberId);

        return ResponseEntity.ok(issueTemplateId);
    }

    @PatchMapping("/{issueTemplateId}")
    public ResponseEntity<Void> updateIssueTemplate(
        @Valid @RequestBody IssueTemplateUpdateRequest request,
        @PathVariable Long teamId,
        @PathVariable Long issueTemplateId,
        @AuthMemberId Long memberId
    ) {
        issueManagementService.updateIssueTemplate(request.toServiceRequest(teamId, issueTemplateId), memberId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{issueTemplateId}")
    public ResponseEntity<IssueTemplateDetailResponse> getIssueTemplate(
        @PathVariable Long teamId,
        @PathVariable Long issueTemplateId,
        @AuthMemberId Long memberId
    ) {
        IssueTemplateDetailResponse issueTemplateResponse = issueManagementService.getIssueTemplate(
            teamId, issueTemplateId, memberId
        );

        return ResponseEntity.ok(issueTemplateResponse);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<IssueTemplateDetailResponse>> getIssueTemplates(
        @PathVariable Long teamId,
        @PathVariable Long projectId,
        @RequestParam(required = false) String type,
        @AuthMemberId Long memberId
    ) {
        List<IssueTemplateDetailResponse> issueTemplateResponse = issueManagementService.getIssueTemplates(
            teamId, memberId, projectId, type
        );

        return ResponseEntity.ok(issueTemplateResponse);
    }

}
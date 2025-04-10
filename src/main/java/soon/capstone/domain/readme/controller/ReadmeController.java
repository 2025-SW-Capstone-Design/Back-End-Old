package soon.capstone.domain.readme.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.readme.controller.dto.request.ReadmeCreateRequest;
import soon.capstone.domain.readme.controller.dto.request.ReadmeDeleteRequest;
import soon.capstone.domain.readme.controller.dto.request.ReadmeUpdateRequest;
import soon.capstone.domain.readme.service.ReadmeService;
import soon.capstone.global.anootation.AuthMemberId;

@RequiredArgsConstructor
@RequestMapping("/api/v1/teams/{teamId}/projects/{projectId}/readme")
@RestController
public class ReadmeController {

    private final ReadmeService readmeService;

    @PostMapping
    public ResponseEntity<Long> createReadme(
        @Valid @RequestBody ReadmeCreateRequest request,
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long projectId
    ) {
        Long readmeId = readmeService.create(request.toServiceRequest(memberId, projectId, teamId));
        return ResponseEntity.ok(readmeId);
    }

    @PatchMapping("/{readmeId}")
    public ResponseEntity<Long> updateReadme(
        @Valid @RequestBody ReadmeUpdateRequest request,
        @AuthMemberId Long memberId,
        @PathVariable Long readmeId,
        @PathVariable Long teamId,
        @PathVariable Long projectId
    ) {
        Long updatedReadmeId = readmeService.update(
            request.toServiceRequest(memberId, readmeId, projectId, teamId)
        );
        return ResponseEntity.ok(updatedReadmeId);
    }

    @DeleteMapping("/{readmeId}")
    public ResponseEntity<Void> deleteReadme(
        @AuthMemberId Long memberId,
        @PathVariable Long readmeId,
        @PathVariable Long teamId,
        @PathVariable Long projectId
    ) {
        readmeService.delete(
            ReadmeDeleteRequest.toServiceRequest(memberId, readmeId, teamId)
        );
        return ResponseEntity.noContent().build();
    }

}
package soon.capstone.domain.readme.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.readme.controller.dto.request.*;
import soon.capstone.domain.readme.service.ReadmeService;
import soon.capstone.domain.readme.service.dto.response.ReadmeDetailResponse;
import soon.capstone.domain.readme.service.dto.response.ReadmeListResponse;
import soon.capstone.global.anootation.AuthMemberId;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/teams/{teamId}")
public class ReadmeController {

    private final ReadmeService readmeService;

    @PostMapping("/projects/{projectId}/readmes")
    public ResponseEntity<Long> createReadme(
        @Valid @RequestBody ReadmeCreateRequest request,
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long projectId
    ) {
        Long readmeId = readmeService.create(request.toServiceRequest(memberId, projectId, teamId));
        return ResponseEntity.ok(readmeId);
    }

    @PatchMapping("/projects/{projectId}/readmes/{readmeId}")
    public ResponseEntity<Long> updateReadme(
        @Valid @RequestBody ReadmeUpdateRequest request,
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long projectId,
        @PathVariable Long readmeId
    ) {
        Long updatedReadmeId = readmeService.update(
            request.toServiceRequest(memberId, readmeId, projectId, teamId)
        );
        return ResponseEntity.ok(updatedReadmeId);
    }

    @DeleteMapping("/readmes/{readmeId}")
    public ResponseEntity<Void> deleteReadme(
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long readmeId
    ) {
        readmeService.delete(ReadmeDeleteRequest.toServiceRequest(memberId, readmeId, teamId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/projects/{projectId}/readmes")
    public ResponseEntity<List<ReadmeListResponse>> getReadmeList(
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long projectId
    ) {
        List<ReadmeListResponse> response = readmeService.getReadmes(
            ReadmesListRequest.toServiceRequest(memberId, projectId, teamId)
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/readmes/{readmeId}")
    public ResponseEntity<ReadmeDetailResponse> getReadmeDetail(
        @AuthMemberId Long memberId,
        @PathVariable Long teamId,
        @PathVariable Long readmeId
    ) {
        ReadmeDetailResponse response = readmeService.getDetail(
            ReadmeDetailRequest.toServiceRequest(memberId, readmeId, teamId)
        );
        return ResponseEntity.ok(response);
    }

}
package soon.capstone.domain.readme.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.readme.controller.dto.request.ReadmeCreateRequest;
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

}
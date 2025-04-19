package soon.capstone.domain.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.project.controller.docs.ProjectControllerDocs;
import soon.capstone.domain.project.service.ProjectService;
import soon.capstone.domain.project.service.dto.response.ProjectDetailResponse;
import soon.capstone.global.anootation.AuthMemberId;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@RestController
public class ProjectController implements ProjectControllerDocs {

    private final ProjectService projectService;

    @GetMapping("/{teamId}")
    public ResponseEntity<List<ProjectDetailResponse>> getProjects(@AuthMemberId Long memberId, @PathVariable Long teamId) {
        return ResponseEntity.ok(projectService.getProjects(memberId, teamId));
    }

}
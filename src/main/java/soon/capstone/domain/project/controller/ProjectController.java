package soon.capstone.domain.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.project.service.ProjectService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@RestController
public class ProjectController {

    private final ProjectService projectService;

}
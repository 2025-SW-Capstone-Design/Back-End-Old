package soon.capstone.domain.readme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/teams/{teamId}/projects/{projectId}/readme")
@RestController
public class ReadmeController {
}
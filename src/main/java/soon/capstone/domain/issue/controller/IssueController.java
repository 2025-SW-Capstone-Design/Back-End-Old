package soon.capstone.domain.issue.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.issue.service.IssueService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/teams/{teamId}/issues")
@RestController
public class IssueController {

    private final IssueService issueService;

}
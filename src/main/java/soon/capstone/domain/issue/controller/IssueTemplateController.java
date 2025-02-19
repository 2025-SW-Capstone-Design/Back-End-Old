package soon.capstone.domain.issue.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.issue.service.IssueTemplateService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/issue/template")
@RestController
public class IssueTemplateController {

    private final IssueTemplateService issueTemplateService;

}
package soon.capstone.domain.issue.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.issue.service.IssueLabelService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/issue/label")
@RestController
public class IssueLabelController {

    private final IssueLabelService issueLabelService;

}
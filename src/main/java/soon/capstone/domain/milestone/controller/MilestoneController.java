package soon.capstone.domain.milestone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.milestone.service.MilestoneService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/milestones")
@RestController
public class MilestoneController {

    private final MilestoneService milestoneService;

}
package soon.capstone.domain.teammember.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.teammember.service.TeamMemberService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/team-members")
@RestController
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

}
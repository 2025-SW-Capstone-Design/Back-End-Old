package soon.capstone;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import soon.capstone.domain.auth.controller.AuthController;
import soon.capstone.domain.auth.service.AuthService;
import soon.capstone.domain.issue.controller.IssueController;
import soon.capstone.domain.issue.controller.IssueLabelController;
import soon.capstone.domain.issue.controller.IssueTemplateController;
import soon.capstone.domain.issue.service.IssueManagementService;
import soon.capstone.domain.milestone.controller.MilestoneController;
import soon.capstone.domain.milestone.service.MilestoneService;
import soon.capstone.domain.project.controller.ProjectController;
import soon.capstone.domain.project.service.ProjectService;
import soon.capstone.domain.readme.controller.ReadmeController;
import soon.capstone.domain.readme.service.ReadmeService;
import soon.capstone.domain.team.controller.TeamController;
import soon.capstone.domain.team.service.team.TeamService;
import soon.capstone.domain.teammember.controller.TeamMemberController;
import soon.capstone.domain.teammember.service.TeamMemberService;

@Import(TestSecurityConfig.class)
@WebMvcTest(
    controllers = {
        AuthController.class,
        TeamController.class,
        ProjectController.class,
        TeamMemberController.class,
        IssueLabelController.class,
        IssueTemplateController.class,
        IssueController.class,
        MilestoneController.class,
        ReadmeController.class
    }
)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected TeamService teamService;

    @MockitoBean
    protected TeamMemberService teamMemberService;

    @MockitoBean
    protected ProjectService projectService;

    @MockitoBean
    protected IssueManagementService issueManagementService;

    @MockitoBean
    protected MilestoneService milestoneService;

    @MockitoBean
    protected ReadmeService readmeService;

}
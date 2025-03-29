package soon.capstone;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import soon.capstone.domain.auth.controller.AuthController;
import soon.capstone.domain.auth.service.AuthService;
import soon.capstone.domain.issue.service.IssueManagementService;
import soon.capstone.domain.project.service.ProjectService;
import soon.capstone.domain.team.controller.TeamController;
import soon.capstone.domain.team.service.team.TeamService;
import soon.capstone.domain.teammember.controller.TeamMemberController;
import soon.capstone.domain.teammember.service.TeamMemberService;

@Import(TestSecurityConfig.class)
@WebMvcTest(
    controllers = {
        AuthController.class,
        TeamController.class,
        TeamMemberController.class
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

}
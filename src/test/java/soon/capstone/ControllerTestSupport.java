package soon.capstone;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import soon.capstone.domain.member.controller.MemberController;
import soon.capstone.domain.member.service.MemberService;

@WebMvcTest(
    controllers = {
        MemberController.class
    },
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected MemberService memberService;

}
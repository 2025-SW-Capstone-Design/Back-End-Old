package soon.capstone.domain.milestone.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.milestone.controller.dto.MilestoneCreateRequest;
import soon.capstone.global.anootation.TestMember;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MilestoneControllerTest extends ControllerTestSupport {

    private final static String BASE_URL = "/api/v1/milestones";

    @TestMember
    @DisplayName("마일스톤을 생성한다.")
    @Test
    void createMilestone() throws Exception {
        // given

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        MilestoneCreateRequest request = MilestoneCreateRequest.builder()
                .teamId(1L)
                .projectId(1L)
                .title("Test")
                .description("Test Description")
                .startDate(startDate)
                .dueDate(dueDate)
                .build();

        given(milestoneService.createMilestone(1L, request.toServiceRequest()))
                .willReturn(1L);

        // expected
        mockMvc.perform(
                        post(BASE_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @TestMember
    @DisplayName("마일스톤을 생성할 때 팀 아이디는 필수이다.")
    @Test
    void createMilestoneWithoutTeamId() throws Exception {
        // given

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        MilestoneCreateRequest request = MilestoneCreateRequest.builder()
                .projectId(1L)
                .title("Test")
                .description("Test Description")
                .startDate(startDate)
                .dueDate(dueDate)
                .build();

        // expected
        mockMvc.perform(
                        post(BASE_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.teamId").value("팀 아이디를 입력해주세요."));
    }

    @TestMember
    @DisplayName("마일스톤을 생성할 때 프로젝트 아이디는 필수이다.")
    @Test
    void createMilestoneWithoutProjectId() throws Exception {
        // given

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        MilestoneCreateRequest request = MilestoneCreateRequest.builder()
                .teamId(1L)
                .title("Test")
                .description("Test Description")
                .startDate(startDate)
                .dueDate(dueDate)
                .build();

        // expected
        mockMvc.perform(
                        post(BASE_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.projectId").value("프로젝트 아이디를 입력해주세요."));
    }

    @TestMember
    @DisplayName("마일스톤을 생성할 때 마일스톤 이름은 필수이다.")
    @Test
    void createMilestoneWithoutTitle() throws Exception {
        // given

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        MilestoneCreateRequest request = MilestoneCreateRequest.builder()
                .teamId(1L)
                .projectId(1L)
                .description("Test Description")
                .startDate(startDate)
                .dueDate(dueDate)
                .build();

        // expected
        mockMvc.perform(
                        post(BASE_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("마일스톤 이름을 입력해주세요."));
    }

    @TestMember
    @DisplayName("마일스톤을 생성할 때 마일스톤 설명은 필수이다.")
    @Test
    void createMilestoneWithoutDescription() throws Exception {
        // given

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        MilestoneCreateRequest request = MilestoneCreateRequest.builder()
                .teamId(1L)
                .projectId(1L)
                .title("Test")
                .startDate(startDate)
                .dueDate(dueDate)
                .build();

        // expected
        mockMvc.perform(
                        post(BASE_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.description").value("마일스톤 설명을 입력해주세요."));
    }

    @TestMember
    @DisplayName("마일스톤을 생성할 때 마일스톤 시작일은 필수이다.")
    @Test
    void createMilestoneWithoutStartDate() throws Exception {
        // given

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        MilestoneCreateRequest request = MilestoneCreateRequest.builder()
                .teamId(1L)
                .projectId(1L)
                .title("Test")
                .description("Test Description")
                .dueDate(dueDate)
                .build();

        // expected
        mockMvc.perform(
                        post(BASE_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.startDate").value("마일스톤 시작일을 입력해주세요."));
    }

    @TestMember
    @DisplayName("마일스톤을 생성할 때 마일스톤 마감일은 필수이다.")
    @Test
    void createMilestoneWithoutDueDate() throws Exception {
        // given
        LocalDateTime startDate = LocalDateTime.now();

        MilestoneCreateRequest request = MilestoneCreateRequest.builder()
                .teamId(1L)
                .projectId(1L)
                .title("Test")
                .description("Test Description")
                .startDate(startDate)
                .build();

        // expected
        mockMvc.perform(
                        post(BASE_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.dueDate").value("마일스톤 마감일을 입력해주세요."));
    }

}
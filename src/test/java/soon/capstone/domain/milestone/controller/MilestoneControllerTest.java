package soon.capstone.domain.milestone.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.milestone.controller.dto.request.MilestoneCreateRequest;
import soon.capstone.domain.milestone.service.dto.response.MilestoneDetailResponse;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.global.anootation.TestMember;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MilestoneControllerTest extends ControllerTestSupport {

    private static final Long TEAM_ID = 1L;
    private static final Long PROJECT_ID = 1L;
    private static final Long MILESTONE_ID = 1L;
    private static final String BASE_URL = "/api/v1/teams/" + TEAM_ID;

    @TestMember
    @DisplayName("마일스톤을 생성한다.")
    @Test
    void createMilestone() throws Exception {
        // given
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        MilestoneCreateRequest request = MilestoneCreateRequest.builder()
                .title("Test")
                .description("Test Description")
                .startDate(startDate)
                .dueDate(dueDate)
                .build();

        given(milestoneService.createMilestone(1L, TEAM_ID, PROJECT_ID, request.toServiceRequest()))
                .willReturn(1L);

        // expected
        mockMvc.perform(
                        post(BASE_URL + "/projects/{projectId}", PROJECT_ID)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @TestMember
    @DisplayName("마일스톤을 생성할 때 마일스톤 이름은 필수이다.")
    @Test
    void createMilestoneWithoutTitle() throws Exception {
        // given
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        MilestoneCreateRequest request = MilestoneCreateRequest.builder()
                .description("Test Description")
                .startDate(startDate)
                .dueDate(dueDate)
                .build();

        // expected
        mockMvc.perform(
                        post(BASE_URL + "/projects/{projectId}", PROJECT_ID)
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
                .title("Test")
                .startDate(startDate)
                .dueDate(dueDate)
                .build();

        // expected
        mockMvc.perform(
                        post(BASE_URL + "/projects/{projectId}", PROJECT_ID)
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
                .title("Test")
                .description("Test Description")
                .dueDate(dueDate)
                .build();

        // expected
        mockMvc.perform(
                        post(BASE_URL + "/projects/{projectId}", PROJECT_ID)
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
                .title("Test")
                .description("Test Description")
                .startDate(startDate)
                .build();

        // expected
        mockMvc.perform(
                        post(BASE_URL + "/projects/{projectId}", PROJECT_ID)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.dueDate").value("마일스톤 마감일을 입력해주세요."));
    }

    @TestMember
    @DisplayName("프로젝트에 속한 마일스톤을 반환한다.")
    @Test
    void getMilestonesByProject() throws Exception {
        // Given
        Long memberId = 1L;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        List<MilestoneResponse> milestones = List.of(
                createMilestoneResponse(1L, startDate, dueDate),
                createMilestoneResponse(2L, startDate, dueDate)
        );

        given(milestoneService.getMilestonesByProject(memberId, TEAM_ID, PROJECT_ID))
                .willReturn(milestones);

        // Expected
        mockMvc.perform(
                        get(BASE_URL + "/projects/{projectId}", PROJECT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].startDate").exists())
                .andExpect(jsonPath("$[0].dueDate").exists())
                .andExpect(jsonPath("$[0].creator").value("nickname"))
                .andExpect(jsonPath("$[1].title").value("Test"))
                .andExpect(jsonPath("$[1].description").value("Description"))
                .andExpect(jsonPath("$[1].startDate").exists())
                .andExpect(jsonPath("$[1].dueDate").exists())
                .andExpect(jsonPath("$[1].creator").value("nickname"));
    }

    @TestMember
    @DisplayName("팀에 속한 마일스톤을 반환한다.")
    @Test
    void getMilestonesByTeam() throws Exception {
        // Given
        Long memberId = 1L;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        List<MilestoneResponse> milestones = List.of(
                createMilestoneResponse(1L, startDate, dueDate),
                createMilestoneResponse(2L, startDate, dueDate)
        );

        given(milestoneService.getMilestonesByTeam(memberId, TEAM_ID))
                .willReturn(milestones);

        // Expected
        mockMvc.perform(
                        get(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].startDate").exists())
                .andExpect(jsonPath("$[0].dueDate").exists())
                .andExpect(jsonPath("$[0].creator").value("nickname"))
                .andExpect(jsonPath("$[1].title").value("Test"))
                .andExpect(jsonPath("$[1].description").value("Description"))
                .andExpect(jsonPath("$[1].startDate").exists())
                .andExpect(jsonPath("$[1].dueDate").exists())
                .andExpect(jsonPath("$[1].creator").value("nickname"));
    }

    @TestMember
    @DisplayName("마일스톤 상세 정보를 반환한다.")
    @Test
    void getMilestoneDetail() throws Exception {
        // Given
        Long memberId = 1L;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        MilestoneDetailResponse milestoneDetail = MilestoneDetailResponse.builder()
                .milestoneId(MILESTONE_ID)
                .title("Test")
                .description("Description")
                .creator("nickname")
                .startDate(startDate)
                .dueDate(dueDate)
                .isCompleted(false)
                .issues(List.of())
                .build();

        given(milestoneService.getMilestoneDetail(memberId, TEAM_ID, MILESTONE_ID))
                .willReturn(milestoneDetail);

        // Expected
        mockMvc.perform(
                        get(BASE_URL + "/milestones/{milestoneId}", MILESTONE_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.milestoneId").value(MILESTONE_ID))
                .andExpect(jsonPath("$.title").value("Test"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.creator").value("nickname"))
                .andExpect(jsonPath("$.startDate").exists())
                .andExpect(jsonPath("$.dueDate").exists())
                .andExpect(jsonPath("$.isCompleted").value(false));
    }

    private MilestoneResponse createMilestoneResponse(Long milestoneId, LocalDateTime startDate, LocalDateTime dueDate) {
        return MilestoneResponse.builder()
                .milestoneId(milestoneId)
                .title("Test")
                .description("Description")
                .creator("nickname")
                .startDate(startDate)
                .dueDate(dueDate)
                .isCompleted(false)
                .build();
    }
}
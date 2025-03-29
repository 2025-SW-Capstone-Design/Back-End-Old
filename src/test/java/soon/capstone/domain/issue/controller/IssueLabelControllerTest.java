package soon.capstone.domain.issue.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.issue.controller.dto.IssueLabelCreateRequest;
import soon.capstone.global.anootation.TestMember;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IssueLabelControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/teams/{teamId}/issue-labels";

    @TestMember
    @DisplayName("이슈 라벨을 생성한다.")
    @Test
    void createIssueLabel() throws Exception {
        // given
        var request = createIssueLabelRequest();
        given(issueManagementService.createIssueLabel(any(), eq(1L)))
            .willReturn(1L);

        // expected
        mockMvc.perform(
                post(BASE_URL, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1));
    }

    @TestMember
    @DisplayName("이슈 라벨을 생성 시 제목은 필수값이다.")
    @Test
    void createIssueLabelWithoutTitle() throws Exception {
        // given
        var request = IssueLabelCreateRequest.builder()
            .color("color")
            .projectId(1L)
            .description("description")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.title").value("제목은 필수 입력 값입니다."));
    }

    @TestMember
    @DisplayName("이슈 라벨을 생성 시 설명은 필수값이다.")
    @Test
    void createIssueLabelWithoutDescription() throws Exception {
        // given
        var request = IssueLabelCreateRequest.builder()
            .color("color")
            .projectId(1L)
            .title("title")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.description").value("설명은 필수 입력 값입니다."));
    }

    @TestMember
    @DisplayName("이슈 라벨을 생성 시 색상은 필수값이다.")
    @Test
    void createIssueLabelWithoutColor() throws Exception {
        // given
        var request = IssueLabelCreateRequest.builder()
            .description("description")
            .projectId(1L)
            .title("title")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.color").value("색상은 필수 입력 값입니다."));
    }

    @TestMember
    @DisplayName("이슈 라벨을 생성 시 프로젝트ID는 양수값이다.")
    @Test
    void createIssueLabelWithoutZeroProjectId() throws Exception {
        // given
        var request = IssueLabelCreateRequest.builder()
            .description("description")
            .projectId(0L)
            .color("color")
            .title("title")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.projectId").value("프로젝트 ID는 1 이상의 값이어야 합니다."));
    }

    @TestMember
    @DisplayName("이슈 라벨을 생성 시 프로젝트ID는 필수값이다.")
    @Test
    void createIssueLabelWithoutProjectId() throws Exception {
        // given
        var request = IssueLabelCreateRequest.builder()
            .description("description")
            .color("color")
            .title("title")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.projectId").value("프로젝트 ID는 필수 입력 값입니다."));
    }

    private IssueLabelCreateRequest createIssueLabelRequest() {
        return IssueLabelCreateRequest.builder()
            .color("color")
            .title("title")
            .projectId(1L)
            .description("description")
            .build();
    }

}
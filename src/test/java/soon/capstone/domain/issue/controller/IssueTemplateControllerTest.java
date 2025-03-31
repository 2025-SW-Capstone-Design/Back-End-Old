package soon.capstone.domain.issue.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.issue.controller.dto.IssueTemplateCreateRequest;
import soon.capstone.domain.issue.controller.dto.IssueTemplateUpdateRequest;
import soon.capstone.domain.issue.service.dto.response.IssueTemplateDetailResponse;
import soon.capstone.global.anootation.TestMember;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IssueTemplateControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/teams/{teamId}/issue-templates";

    @TestMember
    @DisplayName("이슈 템플릿을 생성한다.")
    @Test
    void createIssueTemplate() throws Exception {
        // given
        Long teamId = 1L;
        var request = IssueTemplateCreateRequest.builder()
            .title("title")
            .projectId(1L)
            .content("content")
            .description("description")
            .type("type")
            .build();
        given(issueManagementService.createIssueTemplate(any(), eq(1L)))
            .willReturn(1L);

        // expected
        mockMvc.perform(
                post(BASE_URL, teamId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1));
    }

    @DisplayName("이슈 템플릿을 생성 시 제목은 필수값이다.")
    @Test
    void createIssueTemplateWithoutTitle() throws Exception {
        // given
        Long teamId = 1L;
        var request = IssueTemplateCreateRequest.builder()
            .projectId(1L)
            .content("content")
            .description("description")
            .type("type")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL, teamId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.title").value("제목은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 템플릿을 생성 시 설명은 필수값이다.")
    @Test
    void createIssueTemplateWithoutDescription() throws Exception {
        // given
        Long teamId = 1L;
        var request = IssueTemplateCreateRequest.builder()
            .title("title")
            .projectId(1L)
            .content("content")
            .type("type")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL, teamId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.description").value("설명은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 템플릿을 생성 시 내용은 필수값이다.")
    @Test
    void createIssueTemplateWithoutContent() throws Exception {
        // given
        Long teamId = 1L;
        var request = IssueTemplateCreateRequest.builder()
            .title("title")
            .projectId(1L)
            .description("description")
            .type("type")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL, teamId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.content").value("내용은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 템플릿을 생성 시 타입은 필수값이다.")
    @Test
    void createIssueTemplateWithoutType() throws Exception {
        // given
        Long teamId = 1L;
        var request = IssueTemplateCreateRequest.builder()
            .title("title")
            .projectId(1L)
            .content("content")
            .description("description")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL, teamId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.type").value("타입은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 템플릿을 생성 시 프로젝트 ID는 필수값이다.")
    @Test
    void createIssueTemplateWithoutZeroProjectID() throws Exception {
        // given
        Long teamId = 1L;
        var request = IssueTemplateCreateRequest.builder()
            .title("title")
            .projectId(0L)
            .content("content")
            .description("description")
            .type("type")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL, teamId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.projectId").value("프로젝트 ID는 0보다 커야합니다."));
    }

    @DisplayName("이슈 템플릿을 생성 시 프로젝트 ID는 필수값이다.")
    @Test
    void createIssueTemplateWithoutProjectID() throws Exception {
        // given
        Long teamId = 1L;
        var request = IssueTemplateCreateRequest.builder()
            .title("title")
            .content("content")
            .description("description")
            .type("type")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL, teamId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.projectId").value("프로젝트 ID는 필수 입력 값입니다."));
    }

    @TestMember
    @DisplayName("이슈 템플릿을 수정한다")
    @Test
    void updateIssueTemplate() throws Exception {
        // given
        Long issueTemplateId = 1L;
        Long teamId = 1L;
        var request = IssueTemplateUpdateRequest.builder()
            .title("title")
            .description("description")
            .content("content")
            .type("type")
            .projectId(1L)
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{issueTemplateId}", teamId, issueTemplateId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("이슈 템플릿을 수정 시 제목은 필수값이다.")
    @Test
    void updateIssueTemplateWithoutTitle() throws Exception {
        // given
        Long issueTemplateId = 1L;
        Long teamId = 1L;
        var request = IssueTemplateUpdateRequest.builder()
            .description("description")
            .content("content")
            .type("type")
            .projectId(1L)
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{issueTemplateId}", teamId, issueTemplateId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.title").value("제목은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 템플릿을 수정 시 설명은 필수값이다.")
    @Test
    void updateIssueTemplateWithoutDescription() throws Exception {
        // given
        Long issueTemplateId = 1L;
        Long teamId = 1L;
        var request = IssueTemplateUpdateRequest.builder()
            .title("title")
            .content("content")
            .type("type")
            .projectId(1L)
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{issueTemplateId}", teamId, issueTemplateId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.description").value("설명은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 템플릿을 수정 시 내용은 필수값이다.")
    @Test
    void updateIssueTemplateWithoutContent() throws Exception {
        // given
        Long issueTemplateId = 1L;
        Long teamId = 1L;
        var request = IssueTemplateUpdateRequest.builder()
            .title("title")
            .description("description")
            .type("type")
            .projectId(1L)
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{issueTemplateId}", teamId, issueTemplateId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.content").value("내용은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 템플릿을 수정 시 타입은 필수값이다.")
    @Test
    void updateIssueTemplateWithoutType() throws Exception {
        // given
        Long issueTemplateId = 1L;
        Long teamId = 1L;
        var request = IssueTemplateUpdateRequest.builder()
            .title("title")
            .description("description")
            .content("content")
            .projectId(1L)
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{issueTemplateId}", teamId, issueTemplateId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.type").value("타입은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 템플릿을 수정 시 프로젝트 ID는 필수값이다.")
    @Test
    void updateIssueTemplateWithoutProjectId() throws Exception {
        // given
        Long issueTemplateId = 1L;
        Long teamId = 1L;
        var request = IssueTemplateUpdateRequest.builder()
            .title("title")
            .description("description")
            .content("content")
            .type("type")
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{issueTemplateId}", teamId, issueTemplateId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.projectId").value("프로젝트 ID는 필수 입력 값입니다."));
    }

    @DisplayName("이슈 템플릿을 수정 시 제목은 필수값이다.")
    @Test
    void updateIssueTemplateWithZero() throws Exception {
        // given
        Long issueTemplateId = 1L;
        Long teamId = 1L;
        var request = IssueTemplateUpdateRequest.builder()
            .title("title")
            .description("description")
            .content("content")
            .type("type")
            .projectId(0L)
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{issueTemplateId}", teamId, issueTemplateId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.projectId").value("프로젝트 ID는 0보다 커야합니다."));
    }

    @TestMember
    @DisplayName("ID로 이슈 템플릿을 조회한다.")
    @Test
    void getIssueTemplate() throws Exception {
        // given
        Long teamId = 1L;
        Long issueTemplateId = 1L;
        IssueTemplateDetailResponse response = new IssueTemplateDetailResponse(
            issueTemplateId, "title", "description", "content", "Feature"
        );
        given(issueManagementService.getIssueTemplate(eq(teamId), eq(issueTemplateId), eq(1L)))
            .willReturn(response);

        // expected
        mockMvc.perform(
                get(BASE_URL + "/{issueTemplateId}", teamId, issueTemplateId)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(issueTemplateId))
            .andExpect(jsonPath("$.title").value("title"))
            .andExpect(jsonPath("$.description").value("description"))
            .andExpect(jsonPath("$.content").value("content"))
            .andExpect(jsonPath("$.type").value("Feature"));
    }

    @TestMember
    @DisplayName("프로젝트의 모든 이슈 템플릿 목록을 조회한다.")
    @Test
    void getIssueTemplates() throws Exception {
        // given
        Long teamId = 1L;
        Long projectId = 2L;
        List<IssueTemplateDetailResponse> responses = List.of(
            new IssueTemplateDetailResponse(1L, "title1", "description1", "content1", "Feature"),
            new IssueTemplateDetailResponse(2L, "title2", "description2", "content2", "Refactor")
        );
        given(issueManagementService.getIssueTemplates(eq(teamId), eq(1L), eq(projectId), eq(null)))
            .willReturn(responses);

        // expected
        mockMvc.perform(
                get(BASE_URL + "/projects/{projectId}", teamId, projectId)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("title1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].title").value("title2"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @TestMember
    @DisplayName("타입을 기준으로 필터링된 이슈 템플릿 목록을 조회한다.")
    @Test
    void getIssueTemplatesWithTypeFilter() throws Exception {
        // given
        Long teamId = 1L;
        Long projectId = 2L;
        String type = "Feature";
        List<IssueTemplateDetailResponse> responses = List.of(
            new IssueTemplateDetailResponse(1L, "title", "description", "content", "Feature")
        );
        given(issueManagementService.getIssueTemplates(eq(teamId), eq(1L), eq(projectId), eq(type)))
            .willReturn(responses);

        // expected
        mockMvc.perform(
                get(BASE_URL + "/projects/{projectId}", teamId, projectId)
                    .param("type", type)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("title"))
            .andExpect(jsonPath("$[0].type").value("Feature"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)));
    }

}
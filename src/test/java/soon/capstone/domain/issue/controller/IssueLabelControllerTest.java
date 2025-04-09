package soon.capstone.domain.issue.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.issue.controller.dto.IssueLabelCreateRequest;
import soon.capstone.domain.issue.controller.dto.IssueLabelDeleteRequest;
import soon.capstone.domain.issue.controller.dto.IssueLabelDetailRequest;
import soon.capstone.domain.issue.controller.dto.IssueLabelUpdateRequest;
import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;
import soon.capstone.global.anootation.TestMember;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @TestMember
    @DisplayName("이슈 라벨을 수정한다.")
    @Test
    void updateIssueLabel() throws Exception {
        // given
        var request = createIssueLabelUpdateRequest();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{labelId}", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("이슈 라벨을 수정 할 때 기존 라벨명은 필수값이다.")
    @Test
    void updateIssueLabelWithoutOldTitle() throws Exception {
        // given
        var request = IssueLabelUpdateRequest.builder()
            .color("color")
            .newTitle("newTitle")
            .projectId(1L)
            .description("description")
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{labelId}", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.oldTitle").value("기존 라벨명은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 라벨을 수정 할 때 새 라벨명은 필수값이다.")
    @Test
    void updateIssueLabelWithoutNewTitle() throws Exception {
        // given
        var request = IssueLabelUpdateRequest.builder()
            .color("color")
            .oldTitle("oldTitle")
            .projectId(1L)
            .description("description")
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{labelId}", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.newTitle").value("새 라벨명은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 라벨을 수정 할 때 라벨 설명은 필수값이다.")
    @Test
    void updateIssueLabelWithoutDescription() throws Exception {
        // given
        var request = IssueLabelUpdateRequest.builder()
            .color("color")
            .oldTitle("oldTitle")
            .newTitle("newTitle")
            .projectId(1L)
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{labelId}", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.description").value("라벨 설명은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 라벨을 수정 할 때 라벨 색상은 필수값이다.")
    @Test
    void updateIssueLabelWithoutColor() throws Exception {
        // given
        var request = IssueLabelUpdateRequest.builder()
            .oldTitle("oldTitle")
            .newTitle("newTitle")
            .projectId(1L)
            .description("description")
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{labelId}", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.color").value("라벨 색상은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 라벨을 수정 할 때 오가니제이션명은 필수값이다.")
    @Test
    void updateIssueLabelWithoutOrganizationName() throws Exception {
        // given
        var request = IssueLabelUpdateRequest.builder()
            .color("color")
            .oldTitle("oldTitle")
            .newTitle("newTitle")
            .projectId(1L)
            .description("description")
            .repositoryName("repositoryName")
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{labelId}", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.organizationName").value("오가니제이션명은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 라벨을 수정 할 때 레포지토리명은 필수값이다.")
    @Test
    void updateIssueLabelWithoutRepositoryName() throws Exception {
        // given
        var request = IssueLabelUpdateRequest.builder()
            .color("color")
            .oldTitle("oldTitle")
            .newTitle("newTitle")
            .projectId(1L)
            .description("description")
            .organizationName("organizationName")
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{labelId}", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.repositoryName").value("리포지토리명은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 라벨을 수정 할 때 프로젝트 ID는 필수값이다.")
    @Test
    void updateIssueLabelWithoutProjectId() throws Exception {
        // given
        var request = IssueLabelUpdateRequest.builder()
            .color("color")
            .oldTitle("oldTitle")
            .newTitle("newTitle")
            .description("description")
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{labelId}", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.projectId").value("프로젝트 ID는 필수 입력 값입니다."));
    }

    @DisplayName("이슈 라벨을 수정 할 때 projectId는 양수값이다.")
    @Test
    void updateIssueLabelWithoutZeroProjectId() throws Exception {
        // given
        var request = IssueLabelUpdateRequest.builder()
            .color("color")
            .oldTitle("oldTitle")
            .newTitle("newTitle")
            .projectId(0L)
            .description("description")
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{labelId}", 1L, 1L)
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
    @DisplayName("이슈 라벨을 삭제한다.")
    @Test
    void deleteIssueLabel() throws Exception {
        // given
        var request = createIssueLabelDeleteRequest();

        // expected
        mockMvc.perform(
                delete(BASE_URL + "/{labelId}", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("이슈 라벨을 삭제 할 때 라벨명은 필수값이다.")
    @Test
    void deleteIssueLabelWithoutTitle() throws Exception {
        // given
        var request = IssueLabelDeleteRequest.builder()
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .build();

        // expected
        mockMvc.perform(
                delete(BASE_URL + "/{labelId}", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.title").value("라벨명은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 라벨을 삭제 할 때 오가니제이션명은 필수값이다.")
    @Test
    void deleteIssueLabelWithoutOrganizationName() throws Exception {
        // given
        var request = IssueLabelDeleteRequest.builder()
            .title("title")
            .repositoryName("repositoryName")
            .build();

        // expected
        mockMvc.perform(
                delete(BASE_URL + "/{labelId}", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.organizationName").value("오가니제이션명은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 라벨을 삭제 할 때 레포지토리명은 필수값이다.")
    @Test
    void deleteIssueLabelWithoutRepositoryName() throws Exception {
        // given
        var request = IssueLabelDeleteRequest.builder()
            .title("title")
            .organizationName("organizationName")
            .build();

        // expected
        mockMvc.perform(
                delete(BASE_URL + "/{labelId}", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.repositoryName").value("리포지토리명은 필수 입력 값입니다."));
    }

    @TestMember
    @DisplayName("이슈 라벨 목록을 조회한다.")
    @Test
    void getIssueLabels() throws Exception {
        // given
        var request = IssueLabelDetailRequest.toServiceRequest(1L, 1L);
        given(issueManagementService.getIssueLabels(request, 1L))
            .willReturn(List.of(createIssueLabelDetailResponse()));

        // expected
        mockMvc.perform(
                get(BASE_URL, 1L)
                    .param("projectId", "1")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("name"))
            .andExpect(jsonPath("$[0].color").value("color"))
            .andExpect(jsonPath("$[0].description").value("description"));
    }

    private IssueLabelCreateRequest createIssueLabelRequest() {
        return IssueLabelCreateRequest.builder()
            .color("color")
            .title("title")
            .projectId(1L)
            .description("description")
            .build();
    }

    private IssueLabelUpdateRequest createIssueLabelUpdateRequest() {
        return IssueLabelUpdateRequest.builder()
            .color("color")
            .newTitle("newTitle")
            .oldTitle("oldTitle")
            .projectId(1L)
            .description("description")
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .build();
    }

    private IssueLabelDeleteRequest createIssueLabelDeleteRequest() {
        return IssueLabelDeleteRequest.builder()
            .title("title")
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .build();
    }

    private static IssueLabelDetailResponse createIssueLabelDetailResponse() {
        return IssueLabelDetailResponse.builder()
            .id(1L)
            .name("name")
            .color("color")
            .description("description")
            .build();
    }

}
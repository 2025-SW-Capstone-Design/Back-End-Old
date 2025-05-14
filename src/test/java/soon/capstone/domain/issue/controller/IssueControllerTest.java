package soon.capstone.domain.issue.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.issue.controller.dto.IssueCreateRequest;
import soon.capstone.domain.issue.controller.dto.IssueUpdateRequest;
import soon.capstone.domain.issue.controller.dto.IssueUpdateStatusRequest;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;
import soon.capstone.domain.issue.service.dto.response.IssueDetailWrapperResponse;
import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;
import soon.capstone.global.anootation.TestMember;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IssueControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/teams/{teamId}";

    @TestMember
    @DisplayName("이슈를 생성한다.")
    @Test
    void createIssue() throws Exception {
        // given
        var request = IssueCreateRequest.builder()
            .milestoneId(1L)
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .title("title")
            .content("content")
            .assignees("assignee")
            .labels(List.of("label1", "label2"))
            .build();

        given(issueManagementService.createIssue(any()))
            .willReturn(1L);

        // expected
        mockMvc.perform(
                post(BASE_URL + "/projects/{projectId}/issues", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1));
    }

    @DisplayName("이슈 생성 시 제목은 필수값이다.")
    @Test
    void createIssueWithoutTitle() throws Exception {
        // given
        var request = IssueCreateRequest.builder()
            .milestoneId(1L)
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .content("content")
            .assignees("assignee")
            .labels(List.of("label1", "label2"))
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/projects/{projectId}/issues", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.title").value("제목은 필수 입력 값입니다."));
    }

    @DisplayName("이슈 생성 시 라벨 리스트는 비어 있을 수 없다.")
    @Test
    void createIssueWithEmptyLabels() throws Exception {
        // given
        var request = IssueCreateRequest.builder()
            .milestoneId(1L)
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .title("title")
            .content("content")
            .assignees("assignee")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/projects/{projectId}/issues", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.labels").value("라벨 리스트는 필수 입력 값입니다."));
    }

    @TestMember
    @DisplayName("이슈 생성 시 라벨 리스트는 비어 있을 수 없다.")
    @Test
    void createIssueWithoutLabel() throws Exception {
        // given
        var request = IssueCreateRequest.builder()
            .milestoneId(1L)
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .title("title")
            .content("content")
            .assignees("assignee")
            .labels(List.of(""))
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/projects/{projectId}/issues", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation['labels[0]']").value("라벨은 필수 입력 값입니다."));
    }

    @TestMember
    @DisplayName("이슈를 수정한다.")
    @Test
    void updateIssue() throws Exception {
        // given
        var request = IssueUpdateRequest.builder()
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .title("title")
            .content("content")
            .labels(List.of("label1", "label2"))
            .assignees("assignee")
            .state("close")
            .teamMemberId(1L)
            .milestoneId(1L)
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/issues/{issueId}", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @TestMember
    @DisplayName("이슈의 상태를 CLOSED로 수정한다.")
    @Test
    void updateIssueStatus() throws Exception {
        // given
        var request = IssueUpdateStatusRequest.builder()
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .status("CLOSED")
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/issues/{issueId}/status", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @TestMember
    @DisplayName("이슈 상세 정보를 조회한다.")
    @Test
    void getIssueDetail() throws Exception {
        // given
        var labelResponse = List.of(
            createIssueLabelDetailResponse(1L),
            createIssueLabelDetailResponse(2L)
        );

        var response = IssueDetailWrapperResponse.builder()
            .issueDetail(IssueDetailResponse.builder()
                .issueId(1L)
                .title("title")
                .content("content")
                .creator("creator")
                .status("open")
                .labels(labelResponse)
                .build())
            .teamMemberId(1L)
            .build();

        given(issueManagementService.getIssueDetail(any()))
            .willReturn(response);

        // expected
        mockMvc.perform(
                get(BASE_URL + "/projects/{projectId}/issues/{issueId}", 1L, 1L, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.issueDetail.issueId").value(1L))
            .andExpect(jsonPath("$.issueDetail.title").value("title"))
            .andExpect(jsonPath("$.issueDetail.content").value("content"))
            .andExpect(jsonPath("$.issueDetail.creator").value("creator"))
            .andExpect(jsonPath("$.issueDetail.status").value("open"))
            .andExpect(jsonPath("$.issueDetail.labels").isArray())
            .andExpect(jsonPath("$.issueDetail.labels[0].labelId").value(1L))
            .andExpect(jsonPath("$.issueDetail.labels[1].labelId").value(2L));
    }

    @TestMember
    @DisplayName("이슈 목록을 조회한다")
    @Test
    void getIssues() throws Exception {
        // given
        var response = List.of(
            createIssueDetailResponse(1L, "title1"),
            createIssueDetailResponse(2L, "title2")
        );

        given(issueManagementService.getIssues(any()))
            .willReturn(response);

        // expected
        mockMvc.perform(
                get(BASE_URL + "/projects/{projectId}/issues", 1L, 1L)
                    .queryParam("scope", "team")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].issueId").value(1L))
            .andExpect(jsonPath("$[0].title").value("title1"))
            .andExpect(jsonPath("$[1].issueId").value(2L))
            .andExpect(jsonPath("$[1].title").value("title2"));
    }

    public IssueLabelDetailResponse createIssueLabelDetailResponse(Long id) {
        return IssueLabelDetailResponse.builder()
            .labelId(id)
            .description("description")
            .color("color")
            .name("name")
            .build();
    }

    private IssueDetailResponse createIssueDetailResponse(Long id, String title) {
        return IssueDetailResponse.builder()
            .issueId(id)
            .title(title)
            .content("content")
            .creator("creator")
            .status("close")
            .build();
    }

}
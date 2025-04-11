package soon.capstone.domain.readme.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.readme.controller.dto.request.ReadmeCreateRequest;
import soon.capstone.domain.readme.controller.dto.request.ReadmeUpdateRequest;
import soon.capstone.domain.readme.service.dto.response.ReadmeDetailResponse;
import soon.capstone.domain.readme.service.dto.response.ReadmeListResponse;
import soon.capstone.global.anootation.TestMember;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReadmeControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/teams/{teamId}";

    @TestMember
    @DisplayName("리드미를 생성한다.")
    @Test
    void createReadme() throws Exception {
        // given
        var request = ReadmeCreateRequest.builder()
            .title("title")
            .content("content")
            .build();
        given(readmeService.create(any()))
            .willReturn(1L);

        // expected
        mockMvc.perform(
                post(BASE_URL + "/projects/{projectId}/readmes", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1));
    }

    @TestMember
    @DisplayName("리드미 생성 시 제목은 필수값이다.")
    @Test
    void createReadmeWithoutTitle() throws Exception {
        // given
        var request = ReadmeCreateRequest.builder()
            .content("content")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/projects/{projectId}/readmes", 1L, 1L)
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
    @DisplayName("리드미 생성 시 내용은 필수값이다.")
    @Test
    void createReadmeWithoutContent() throws Exception {
        // given
        var request = ReadmeCreateRequest.builder()
            .title("title")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/projects/{projectId}/readmes", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.content").value("내용은 필수 입력 값입니다."));
    }

    @TestMember
    @DisplayName("리드미를 업데이트한다.")
    @Test
    void updateReadme() throws Exception {
        // given
        var request = ReadmeUpdateRequest.builder()
            .title("new title")
            .content("new content")
            .build();
        given(readmeService.update(any()))
            .willReturn(1L);

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/projects/{projectId}/readmes/{readmeId}", 1L, 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1));
    }

    @TestMember
    @DisplayName("리드미 업데이트 시 제목은 필수값이다.")
    @Test
    void updateReadmeWithoutTitle() throws Exception {
        // given
        var request = ReadmeUpdateRequest.builder()
            .content("new content")
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/projects/{projectId}/readmes/{readmeId}", 1L, 1L, 1L)
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
    @DisplayName("리드미 업데이트 시 내용은 필수값이다.")
    @Test
    void updateReadmeWithoutContent() throws Exception {
        // given
        var request = ReadmeUpdateRequest.builder()
            .title("new title")
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/projects/{projectId}/readmes/{readmeId}", 1L, 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.content").value("내용은 필수 입력 값입니다."));
    }

    @TestMember
    @DisplayName("리드미를 삭제한다.")
    @Test
    void deleteReadme() throws Exception {
        // given
        doNothing()
            .when(readmeService)
            .delete(any());

        // expected
        mockMvc.perform(
                delete(BASE_URL + "/readmes/{readmeId}", 1L, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @TestMember
    @DisplayName("프로젝트의 리드미 목록을 조회한다.")
    @Test
    void getReadmeList() throws Exception {
        // given
        List<ReadmeListResponse> responses = List.of(
            createReadmeListResponse(1L, "Title 1", 1, false),
            createReadmeListResponse(2L, "Title 2", 2, true)
        );

        given(readmeService.getReadmes(any()))
            .willReturn(responses);

        // expected
        mockMvc.perform(
                get(BASE_URL + "/projects/{projectId}/readmes", 1L, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].readmeId").value(1))
            .andExpect(jsonPath("$[0].title").value("Title 1"))
            .andExpect(jsonPath("$[0].version").value(1))
            .andExpect(jsonPath("$[0].writer").value("member"))
            .andExpect(jsonPath("$[0].isLatest").value(false))
            .andExpect(jsonPath("$[1].readmeId").value(2))
            .andExpect(jsonPath("$[1].title").value("Title 2"))
            .andExpect(jsonPath("$[1].version").value(2))
            .andExpect(jsonPath("$[1].writer").value("member"))
            .andExpect(jsonPath("$[1].isLatest").value(true));
    }

    @TestMember
    @DisplayName("리드미 상세 정보를 조회한다.")
    @Test
    void getReadmeDetail() throws Exception {
        // given
        ReadmeDetailResponse response = ReadmeDetailResponse.builder()
            .readmeId(1L)
            .title("README Title")
            .content("README Content")
            .version(1)
            .writer("member")
            .isLatest(true)
            .projectName("projectName")
            .build();

        given(readmeService.getDetail(any()))
            .willReturn(response);

        // expected
        mockMvc.perform(
                get(BASE_URL + "/readmes/{readmeId}", 1L, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("README Title"))
            .andExpect(jsonPath("$.content").value("README Content"))
            .andExpect(jsonPath("$.version").value(1))
            .andExpect(jsonPath("$.writer").value("member"));
    }

    @TestMember
    @DisplayName("리드미를 이전 버전으로 롤백한다.")
    @Test
    void rollbackReadme() throws Exception {
        // given
        given(readmeService.rollback(any()))
            .willReturn(3L);

        // expected
        mockMvc.perform(
                post(BASE_URL + "/projects/{projectId}/readmes/{readmeId}/rollback", 1L, 1L, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(3));
    }

    private ReadmeListResponse createReadmeListResponse(long readmeId, String title, int version, boolean isLatest) {
        return ReadmeListResponse.builder()
            .readmeId(readmeId)
            .title(title).
            version(version).
            writer("member").
            isLatest(isLatest).
            build();
    }

}
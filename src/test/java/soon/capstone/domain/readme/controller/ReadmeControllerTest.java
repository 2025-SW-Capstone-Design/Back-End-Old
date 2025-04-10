package soon.capstone.domain.readme.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.readme.controller.dto.request.ReadmeCreateRequest;
import soon.capstone.domain.readme.controller.dto.request.ReadmeUpdateRequest;
import soon.capstone.global.anootation.TestMember;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReadmeControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/teams/{teamId}/projects/{projectId}/readme";

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
                post(BASE_URL, 1L, 1L)
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
                post(BASE_URL, 1L, 1L)
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
                post(BASE_URL, 1L, 1L)
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
                patch(BASE_URL + "/{readmeId}", 1L, 1L, 1L)
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
                patch(BASE_URL + "/{readmeId}", 1L, 1L, 1L)
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
                patch(BASE_URL + "/{readmeId}", 1L, 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.content").value("내용은 필수 입력 값입니다."));
    }

}
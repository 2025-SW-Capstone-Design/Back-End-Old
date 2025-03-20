package soon.capstone.domain.auth.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.auth.controller.dto.ReissueTokenRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/auth";

    @DisplayName("JWT를 재발급 한다")
    @Test
    void reissueToken() throws Exception {
        // given
        ReissueTokenRequest request = ReissueTokenRequest.builder()
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .build();

        // expect
        mockMvc.perform(
                post(BASE_URL + "/reissue")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("JWT를 재발급 할 경우 액세스 토큰은 필수값이다.")
    @Test
    void reissueTokenWithoutAccessToken() throws Exception {
        // given
        ReissueTokenRequest request = ReissueTokenRequest.builder()
            .refreshToken("refreshToken")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/reissue")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.accessToken").value("액세스 토큰을 입력해주세요"));
    }

    @DisplayName("JWT를 재발급 할 경우 리프레시 토큰은 필수값이다.")
    @Test
    void reissueTokenWithoutRefreshToken() throws Exception {
        // given
        ReissueTokenRequest request = ReissueTokenRequest.builder()
            .accessToken("accessToken")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/reissue")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.refreshToken").value("리프레시 토큰을 입력해주세요"));
    }

}
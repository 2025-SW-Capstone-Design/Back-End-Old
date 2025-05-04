package soon.capstone.infrastructure.openvidu.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.global.anootation.TestMember;
import soon.capstone.infrastructure.openvidu.controller.dto.request.OpenViduGenerateTokenRequest;
import soon.capstone.infrastructure.openvidu.controller.dto.request.OpenViduWebhookEventRequest;
import soon.capstone.infrastructure.openvidu.service.dto.response.OpenViduGenerateTokenResponse;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OpenViduControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/open-vidu";

    @TestMember
    @DisplayName("토큰을 생성한다")
    @Test
    void createToken() throws Exception {
        // given
        OpenViduGenerateTokenRequest request = OpenViduGenerateTokenRequest.builder()
            .roomName("testRoom")
            .build();

        OpenViduGenerateTokenResponse response = OpenViduGenerateTokenResponse.builder()
            .token("testToken")
            .roomName("testRoom")
            .memberId(1L)
            .build();

        given(openViduApiService.generateOpenViduToken(request.toServiceRequest(1L)))
            .willReturn(response);

        // expected
        mockMvc.perform(
                post(BASE_URL + "/token")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("testToken"))
            .andExpect(jsonPath("$.roomName").value("testRoom"))
            .andExpect(jsonPath("$.memberId").value(1L));
    }

    @TestMember
    @DisplayName("웹훅 이벤트를 처리한다")
    @Test
    void processesWebhookEvent() throws Exception {
        OpenViduWebhookEventRequest request = OpenViduWebhookEventRequest.builder()
            .body("{\"event\":\"room_started\"}")
            .reservedAt(LocalDateTime.now())
            .build();
        String openViduToken = "testToken";

        mockMvc.perform(
                post(BASE_URL + "/webhook/1")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType("application/webhook+json")
                    .header("X-OpenVidu-Token", openViduToken)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }


}
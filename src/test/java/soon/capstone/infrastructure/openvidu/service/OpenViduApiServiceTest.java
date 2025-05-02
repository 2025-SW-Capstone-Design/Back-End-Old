package soon.capstone.infrastructure.openvidu.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduGenerateTokenServiceRequest;
import soon.capstone.infrastructure.openvidu.service.dto.response.OpenViduGenerateTokenResponse;

import static org.assertj.core.api.Assertions.assertThat;

class OpenViduApiServiceTest extends IntegrationTestSupport {

    @MockitoBean
    private OpenViduApiService openViduApiService;

    @DisplayName("OpenVidu의 토큰을 생성한다")
    @Test
    void generateOpenViduToken() {
        // given
        var request = OpenViduGenerateTokenServiceRequest.builder()
            .roomName("roomName")
            .memberName("memberName")
            .build();

        BDDMockito.given(openViduApiService.generateOpenViduToken(request))
            .willReturn(OpenViduGenerateTokenResponse.builder()
                .jwt("jwt")
                .roomName("roomName")
                .memberName("memberName")
                .build());

        // when
        var response = openViduApiService.generateOpenViduToken(request);

        // then
        assertThat(response).isNotNull()
            .extracting("jwt", "roomName", "memberName")
            .containsExactlyInAnyOrder("jwt", "roomName", "memberName");
    }

}
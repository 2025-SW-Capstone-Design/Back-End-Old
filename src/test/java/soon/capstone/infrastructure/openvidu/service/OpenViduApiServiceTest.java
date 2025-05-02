package soon.capstone.infrastructure.openvidu.service;

import livekit.LivekitModels;
import livekit.LivekitWebhook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.global.exception.common.InvalidRequest;
import soon.capstone.infrastructure.openvidu.handler.OpenViduWebhookEventHandler;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduGenerateTokenServiceRequest;
import soon.capstone.infrastructure.openvidu.service.dto.response.OpenViduGenerateTokenResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

        given(openViduApiService.generateOpenViduToken(request))
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

    @DisplayName("방 생성 이벤트를 처리한다.")
    @Test
    void handleWebhookEvent() {
        // given
        var room = LivekitModels.Room.newBuilder()
            .setName("roomName")
            .build();

        var event = LivekitWebhook.WebhookEvent.newBuilder()
            .setEvent("room_started")
            .setRoom(room)
            .build();

        OpenViduWebhookEventHandler mockHandler = mock(OpenViduWebhookEventHandler.class);
        given(mockHandler.support("room_started"))
            .willReturn(true);

        OpenViduApiService service = new OpenViduApiService(List.of(mockHandler));

        // when
        service.handleWebhookEvent(event);

        // then
        verify(mockHandler).handle(event);
    }

    @DisplayName("지원되지 않는 이벤트를 처리 할 경우 예외가 발생한다.")
    @Test
    void handleWebhookEventForUnsupportedEvent() {
        // given
        var event = LivekitWebhook.WebhookEvent.newBuilder()
            .setEvent("unsupported_event")
            .build();

        OpenViduApiService service = new OpenViduApiService(List.of());

        // expected
        assertThatThrownBy(() -> service.handleWebhookEvent(event))
            .isInstanceOf(InvalidRequest.class)
            .hasMessage("잘못된 요청입니다.");
    }

}
package soon.capstone.infrastructure.openvidu.service;

import io.livekit.server.WebhookReceiver;
import livekit.LivekitWebhook.WebhookEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.global.exception.common.InvalidRequest;
import soon.capstone.infrastructure.openvidu.handler.ParticipantJoinedEventHandler;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduGenerateTokenServiceRequest;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;
import soon.capstone.infrastructure.openvidu.service.dto.response.OpenViduGenerateTokenResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class OpenViduApiServiceTest extends IntegrationTestSupport {

    @Autowired
    private OpenViduApiService openViduApiService;

    @MockitoSpyBean
    private OpenViduApiService spyOpenViduApiService;

    @MockitoBean
    private ParticipantJoinedEventHandler participantJoinedEventHandler;

    @MockitoBean
    private WebhookReceiver receiver;

    @DisplayName("OpenVidu의 토큰을 생성한다")
    @Test
    void generateOpenViduToken() {
        // given
        var request = OpenViduGenerateTokenServiceRequest.builder()
            .roomName("roomName")
            .memberId(1L)
            .build();

        given(spyOpenViduApiService.generateOpenViduToken(request))
            .willReturn(OpenViduGenerateTokenResponse.builder()
                .token("jwt")
                .roomName("roomName")
                .memberId(1L)
                .build());

        // when
        var response = spyOpenViduApiService.generateOpenViduToken(request);

        // then
        assertThat(response).isNotNull()
            .extracting("token", "roomName", "memberId")
            .containsExactlyInAnyOrder("jwt", "roomName", 1L);
    }

    @DisplayName("방 참여 이벤트를 처리한다.")
    @Test
    void handleWebhookEvent() {
        // given
        String body = "{\"event\":\"participant_joined\"}";
        OpenViduWebhookEventServiceRequest request = createOpenViduWebhookEventServiceRequest(body);

        WebhookEvent event = WebhookEvent.newBuilder()
            .setEvent("participant_joined")
            .build();
        given(receiver.receive(body, request.openViduToken())).willReturn(event);
        given(participantJoinedEventHandler.support("participant_joined")).willReturn(true);
        given(participantJoinedEventHandler.handle(event, request)).willReturn(123L);

        // when
        Long roomId = openViduApiService.handleWebhookEvent(request);

        // then
        assertThat(roomId).isNotNull().isEqualTo(123L);
        verify(participantJoinedEventHandler).handle(event, request);
    }

    @DisplayName("지원되지 않는 이벤트를 처리 할 경우 예외가 발생한다.")
    @Test
    void handleWebhookEventForUnsupportedEvent() {
        // given
        String body = "{\"event\":\"unsupported_event\"}";
        OpenViduWebhookEventServiceRequest request = createOpenViduWebhookEventServiceRequest(body);

        doThrow(new InvalidRequest())
            .when(openViduApiService)
            .handleWebhookEvent(request);

        // expected
        assertThatThrownBy(() -> openViduApiService.handleWebhookEvent(request))
            .isInstanceOf(InvalidRequest.class)
            .hasMessage("잘못된 요청입니다.");
    }

    private OpenViduWebhookEventServiceRequest createOpenViduWebhookEventServiceRequest(String body) {
        return OpenViduWebhookEventServiceRequest.builder()
            .body(body)
            .openViduToken("openViduToken")
            .memberId(1L)
            .teamId(1L)
            .build();
    }

}
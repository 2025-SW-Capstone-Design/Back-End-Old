package soon.capstone.infrastructure.openvidu.service;

import io.livekit.server.WebhookReceiver;
import livekit.LivekitModels.ParticipantInfo;
import livekit.LivekitWebhook.WebhookEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.global.exception.common.InvalidRequest;
import soon.capstone.infrastructure.openvidu.handler.ParticipantJoinedEventHandler;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduGenerateTokenServiceRequest;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static soon.capstone.infrastructure.openvidu.common.OpenViduEventType.PARTICIPANT_JOINED;

class OpenViduApiServiceTest extends IntegrationTestSupport {

    @Autowired
    private OpenViduApiService openViduApiService;

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
            .teamId(1L)
            .memberId(1L)
            .build();

        // when
        var response = openViduApiService.generateOpenViduToken(request);

        // then
        assertThat(response)
            .isNotNull()
            .extracting("roomName", "memberId", "teamId")
            .containsExactlyInAnyOrder("roomName", 1L, 1L);

        assertThat(response.token())
            .isNotNull();
    }

    @DisplayName("방 참여 이벤트를 처리한다.")
    @Test
    void handleWebhookEvent() {
        // given
        Long teamId = 1L;
        Long memberId = 1L;
        String identity = memberId + ":" + teamId;
        String body = "{\"event\":" + PARTICIPANT_JOINED + "}";
        OpenViduWebhookEventServiceRequest request = createOpenViduWebhookEventServiceRequest(body);

        WebhookEvent event = WebhookEvent.newBuilder()
            .setEvent(PARTICIPANT_JOINED.getEventType())
            .setParticipant(
                ParticipantInfo.newBuilder()
                    .setIdentity(identity)
                    .build()
            )
            .build();

        given(receiver.receive(body, request.openViduToken())).willReturn(event);
        given(participantJoinedEventHandler.support(PARTICIPANT_JOINED.getEventType()))
            .willReturn(true);

        // when
        openViduApiService.handleWebhookEvent(request);

        // then
        verify(participantJoinedEventHandler).handle(event, teamId, memberId);
    }

    @DisplayName("지원되지 않는 이벤트를 처리 할 경우 예외가 발생한다.")
    @Test
    void handleWebhookEventForUnsupportedEvent() {
        // given
        String body = "{\"event\":\"unsupported_event\"}";
        OpenViduWebhookEventServiceRequest request = createOpenViduWebhookEventServiceRequest(body);

        // expected
        assertThatThrownBy(() -> openViduApiService.handleWebhookEvent(request))
            .isInstanceOf(InvalidRequest.class)
            .hasMessage("잘못된 요청입니다.");
    }

    private OpenViduWebhookEventServiceRequest createOpenViduWebhookEventServiceRequest(String body) {
        return OpenViduWebhookEventServiceRequest.builder()
            .body(body)
            .openViduToken("openViduToken")
            .build();
    }

}
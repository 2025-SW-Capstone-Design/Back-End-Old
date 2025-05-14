package soon.capstone.infrastructure.openvidu.service;

import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import io.livekit.server.WebhookReceiver;
import livekit.LivekitWebhook.WebhookEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import soon.capstone.global.exception.common.InvalidRequest;
import soon.capstone.infrastructure.openvidu.handler.OpenViduWebhookEventHandler;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduGenerateTokenServiceRequest;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduWebhookEventServiceRequest;
import soon.capstone.infrastructure.openvidu.service.dto.response.OpenViduGenerateTokenResponse;

import java.util.List;

@Slf4j
@Service
public class OpenViduApiService {

    private final String apiKey;
    private final String apiSecret;
    private final List<OpenViduWebhookEventHandler> eventHandlers;
    private final WebhookReceiver webhookReceiver;

    public OpenViduApiService(
        @Value("${livekit.api.key}") String apiKey,
        @Value("${livekit.api.secret}") String apiSecret,
        List<OpenViduWebhookEventHandler> eventHandlers,
        WebhookReceiver webhookReceiver
    ) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.eventHandlers = eventHandlers;
        this.webhookReceiver = webhookReceiver;
    }

    public OpenViduGenerateTokenResponse generateOpenViduToken(OpenViduGenerateTokenServiceRequest request) {
        AccessToken token = new AccessToken(apiKey, apiSecret);
        token.setName(request.roomName());
        token.setIdentity(request.memberId() + ":" + request.teamId());
        token.addGrants(new RoomJoin(true), new RoomName(request.roomName()));

        return OpenViduGenerateTokenResponse.builder()
            .token(token.toJwt())
            .roomName(request.roomName())
            .memberId(request.memberId())
            .teamId(request.teamId())
            .build();
    }

    public void handleWebhookEvent(OpenViduWebhookEventServiceRequest request) {
        try {
            WebhookEvent event = webhookReceiver.receive(request.body(), request.openViduToken());

            String identity = event.getParticipant().getIdentity();
            log.info("identity: {}", identity);

            if (identity == null || identity.isBlank() || !identity.contains(":")) {
                log.error("잘못된 identity 값: {}", identity);
                throw new IllegalArgumentException("잘못된 identity 값이 전달되었습니다.");
            }

            String[] split = identity.split(":");
            Long memberId = Long.parseLong(split[0]);
            Long teamId = Long.parseLong(split[1]);


            log.info("웹훅 요청 처리 시작 - body{} memberId: {}, openViduToken: {}, teamId: {}", request.body(), memberId, request.openViduToken(), teamId);

            eventHandlers.stream()
                .filter(handler -> handler.support(event.getEvent()))
                .findFirst()
                .ifPresentOrElse(
                    handler -> {
                        log.info("이벤트 처리 중 - memberId: {}, event: {}", memberId, event.getEvent());
                        handler.handle(event, request);
                    },
                    () -> {
                        log.error("지원하지 않는 이벤트 타입 - memberId: {}, event: {}", memberId, event.getEvent());
                        throw new InvalidRequest();
                    }
                );
        } catch (Exception e) {
            log.error("웹훅 이벤트 처리 중 오류 발생 - 오류: ", e);
//            throw new InvalidRequest();
        }
    }

}
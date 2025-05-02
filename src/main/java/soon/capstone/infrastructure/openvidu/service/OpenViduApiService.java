package soon.capstone.infrastructure.openvidu.service;

import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import livekit.LivekitWebhook.WebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import soon.capstone.global.exception.common.InvalidRequest;
import soon.capstone.infrastructure.openvidu.handler.OpenViduWebhookEventHandler;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduGenerateTokenServiceRequest;
import soon.capstone.infrastructure.openvidu.service.dto.response.OpenViduGenerateTokenResponse;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OpenViduApiService {

    @Value("${livekit.api.key}")
    private String apiKey;

    @Value("${livekit.api.secret}")
    private String apiSecret;

    private final List<OpenViduWebhookEventHandler> eventHandlers;

    public OpenViduGenerateTokenResponse generateOpenViduToken(OpenViduGenerateTokenServiceRequest request) {
        AccessToken token = new AccessToken(apiKey, apiSecret);
        token.setName(request.roomName());
        token.setIdentity(String.valueOf(request.memberId()));
        token.addGrants(new RoomJoin(true), new RoomName(request.roomName()));

        return OpenViduGenerateTokenResponse.builder()
            .token(token.toJwt())
            .roomName(request.roomName())
            .memberId(request.memberId())
            .build();
    }

    public void handleWebhookEvent(WebhookEvent event) {
        eventHandlers.stream()
            .filter(handler -> handler.support(event.getEvent()))
            .findFirst()
            .ifPresentOrElse(
                handler -> handler.handle(event),
                () -> {
                    log.error("잘못된 이벤트: {}", event.getEvent());
                    throw new InvalidRequest();
                }
            );
    }

}
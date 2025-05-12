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

import java.util.Base64;
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
        token.setIdentity(String.valueOf(request.memberId()));
        token.addGrants(new RoomJoin(true), new RoomName(request.roomName()));

        return OpenViduGenerateTokenResponse.builder()
            .token(token.toJwt())
            .roomName(request.roomName())
            .memberId(request.memberId())
            .build();
    }

    public boolean isValidBase64(String token) {
        try {
            // JWT의 3부분이 Base64로 인코딩되어 있는지 확인
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                // 헤더, 페이로드, 서명 부분이 모두 Base64 형식이어야 합니다.
                Base64.getDecoder().decode(parts[0]); // 헤더
                Base64.getDecoder().decode(parts[1]); // 페이로드
                Base64.getDecoder().decode(parts[2]); // 서명
                return true;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    public Long handleWebhookEvent(OpenViduWebhookEventServiceRequest request) {
        try {
            // Bearer 부분 제거
            String token = request.openViduToken().replace("Bearer ", "");

            // JWT 토큰이 올바른 형식인지 확인
            if (!isValidBase64(token)) {
                log.error("유효하지 않은 Base64 토큰 - memberId: {}", request.memberId());
                throw new IllegalArgumentException("유효하지 않은 Base64 토큰");
            }

            log.info("웹훅 요청 처리 시작 - memberId: {}, openViduToken: {}", request.memberId(), token);

            WebhookEvent event = webhookReceiver.receive(request.body(), token);

            return eventHandlers.stream()
                .filter(handler -> handler.support(event.getEvent()))
                .findFirst()
                .map(handler -> {
                    log.info("이벤트 처리 중 - memberId: {}, event: {}", request.memberId(), event.getEvent());
                    return handler.handle(event, request);
                })
                .orElseThrow(() -> {
                    log.error("지원하지 않는 이벤트 타입 - memberId: {}, event: {}", request.memberId(), event.getEvent());
                    return new InvalidRequest();
                });
        } catch (Exception e) {
            log.error("웹훅 이벤트 처리 중 오류 발생 - memberId: {}, 오류: {}", request.memberId(), e.getMessage());
            throw new IllegalArgumentException("웹훅 이벤트 처리 중 오류 발생", e);
        }
    }


//    public Long handleWebhookEvent(OpenViduWebhookEventServiceRequest request) {
//        try {
//            log.info("웹훅 요청 처리 시작 - body{} memberId: {}, openViduToken: {}", request.body(), request.memberId(), request.openViduToken());
//
//            WebhookEvent event = webhookReceiver.receive(request.body(), request.openViduToken());
//
//            return eventHandlers.stream()
//                .filter(handler -> handler.support(event.getEvent()))
//                .findFirst()
//                .map(handler -> {
//                    log.info("이벤트 처리 중 - memberId: {}, event: {}", request.memberId(), event.getEvent());
//                    return handler.handle(event, request);
//                })
//                .orElseThrow(() -> {
//                    log.error("지원하지 않는 이벤트 타입 - memberId: {}, event: {}", request.memberId(), event.getEvent());
//                    return new InvalidRequest();
//                });
//        } catch (Exception e) {
//            log.error("웹훅 이벤트 처리 중 오류 발생 - memberId: {}, 오류: {}", request.memberId(), e.getMessage());
//            throw new InvalidRequest();
//        }
//    }

}
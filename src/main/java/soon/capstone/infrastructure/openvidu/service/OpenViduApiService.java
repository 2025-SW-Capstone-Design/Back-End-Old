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
import soon.capstone.infrastructure.redis.openvidu.repository.TemporaryRoomIdentityRepository;

import java.util.List;

import static soon.capstone.infrastructure.openvidu.common.OpenViduEventType.ROOM_FINISHED;
import static soon.capstone.infrastructure.openvidu.common.OpenViduEventType.ROOM_STARTED;

@Slf4j
@Service
public class OpenViduApiService {

    private final String apiKey;
    private final String apiSecret;
    private final List<OpenViduWebhookEventHandler> eventHandlers;
    private final WebhookReceiver webhookReceiver;
    private final TemporaryRoomIdentityRepository temporaryRoomIdentityRepository;

    public OpenViduApiService(
        @Value("${livekit.api.key}") String apiKey,
        @Value("${livekit.api.secret}") String apiSecret,
        List<OpenViduWebhookEventHandler> eventHandlers,
        WebhookReceiver webhookReceiver,
        TemporaryRoomIdentityRepository temporaryRoomIdentityRepository
    ) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.eventHandlers = eventHandlers;
        this.webhookReceiver = webhookReceiver;
        this.temporaryRoomIdentityRepository = temporaryRoomIdentityRepository;
    }

    public OpenViduGenerateTokenResponse generateOpenViduToken(OpenViduGenerateTokenServiceRequest request) {
        AccessToken token = new AccessToken(apiKey, apiSecret);
        token.setName(request.roomName());
        token.setIdentity(request.memberId() + ":" + request.teamId());
        token.addGrants(new RoomJoin(true), new RoomName(request.roomName()));

        log.info("openVidu 토큰 생성 완료 - roomName: {}, memberId: {}, teamId: {}, token: {}",
            request.roomName(),
            request.memberId(),
            request.teamId(),
            token.toJwt()
        );

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

            log.info("웹훅 이벤트 수신 - event: {}", event.getEvent());
            Long memberId;
            Long teamId;
            if (requiresIdentity(event)) {
                String identity = getIdentity(event);
                memberId = extractIdFromIdentity(identity, 0);
                teamId = extractIdFromIdentity(identity, 1);
                log.info("이벤트 identity 정보 - memberId: {}, teamId: {}", memberId, teamId);
            } else {
                teamId = null;
                memberId = null;
            }

            eventHandlers.stream()
                .filter(handler -> handler.support(event.getEvent()))
                .findFirst()
                .ifPresentOrElse(
                    handler -> {
                        log.debug("이벤트 핸들러 실행 - handler: {}, event: {}", handler.getClass().getSimpleName(), event.getEvent());
                        handler.handle(event, teamId, memberId);
                    },
                    () -> {
                        log.warn("지원하지 않는 이벤트 타입 - event: {}", event.getEvent());
                        throw new InvalidRequest();
                    }
                );

        } catch (InvalidRequest e) {
            log.error("잘못된 요청 - message: {}, body: {}", e.getMessage(), request.body(), e);
            throw e;
        } catch (Exception e) {
            log.error("웹훅 처리 중 예외 발생 - body: {}", request.body(), e);
            throw new InvalidRequest();
        }
    }

    private boolean requiresIdentity(WebhookEvent event) {
        return switch (event.getEvent()) {
            case "participant_joined", "participant_left", "track_published", "room_finished" -> true;
            default -> false;
        };
    }


    private String getIdentity(WebhookEvent event) {
        if (ROOM_FINISHED.getEventType().equals(event.getEvent())) {
            log.info("방 종료 이벤트 수신 - event: {}", event.getEvent());
            return temporaryRoomIdentityRepository.findById(event.getRoom().getSid()).getIdentity();
        }

        if (event.getParticipant() == null) {
            throw new InvalidRequest();
        }

        return event.getParticipant().getIdentity();
    }


    private Long extractIdFromIdentity(String identity, int index) {
        return Long.parseLong(identity.split(":")[index]); // memberId:teamId
    }

}
package soon.capstone.infrastructure.openvidu.service.dto.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OpenViduWebhookEventServiceRequest(

    String body,
    Long memberId,
    Long teamId,
    LocalDateTime reservedAt,
    String openViduToken

) {

}
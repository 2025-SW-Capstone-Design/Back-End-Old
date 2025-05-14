package soon.capstone.infrastructure.openvidu.service.dto.request;

import lombok.Builder;

@Builder
public record OpenViduWebhookEventServiceRequest(

    String body,
    Long memberId,
    Long teamId,
    String openViduToken

) {

}
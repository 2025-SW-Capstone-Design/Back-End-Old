package soon.capstone.infrastructure.openvidu.service.dto.response;

import lombok.Builder;

@Builder
public record OpenViduGenerateTokenResponse(

    String token,
    String roomName,
    Long memberId

) {

}
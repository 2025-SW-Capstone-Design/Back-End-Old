package soon.capstone.infrastructure.openvidu.service.dto.response;

import lombok.Builder;

@Builder
public record OpenViduGenerateTokenResponse(

    String jwt,
    String roomName,
    Long memberId

) {

}
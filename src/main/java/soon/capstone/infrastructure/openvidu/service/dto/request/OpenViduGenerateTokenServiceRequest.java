package soon.capstone.infrastructure.openvidu.service.dto.request;

import lombok.Builder;

@Builder
public record OpenViduGenerateTokenServiceRequest(

    String roomName,
    String memberName

) {
}
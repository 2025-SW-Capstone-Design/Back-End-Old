package soon.capstone.infrastructure.openvidu.controller.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import soon.capstone.infrastructure.openvidu.service.dto.request.OpenViduGenerateTokenServiceRequest;

@Builder
public record OpenViduGenerateTokenRequest(

    @NotEmpty(message = "방 이름은 필수입니다.")
    String roomName

) {

    public OpenViduGenerateTokenServiceRequest toServiceRequest(Long memberId, Long teamId) {
        return OpenViduGenerateTokenServiceRequest.builder()
            .roomName(roomName)
            .memberId(memberId)
            .teamId(teamId)
            .build();
    }

}
package soon.capstone.global.domain.auth.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import soon.capstone.global.domain.auth.service.dto.request.ReissueTokenServiceRequest;

@Builder
public record ReissueTokenRequest(

    @NotEmpty(message = "액세스 토큰을 입력해주세요")
    String accessToken,

    @NotEmpty(message = "리프레시 토큰을 입력해주세요")
    String refreshToken

) {

    public ReissueTokenServiceRequest toServiceRequest() {
        return ReissueTokenServiceRequest.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

}
package soon.capstone.global.domain.auth.service.dto.request;

import lombok.Builder;

@Builder
public record ReissueTokenServiceRequest(

    String accessToken,
    String refreshToken

) {
}
package soon.capstone.global.security.jwt.dto.response;

import lombok.Builder;

@Builder
public record TokenResponse(

    String accessToken,

    String refreshToken

) {

}
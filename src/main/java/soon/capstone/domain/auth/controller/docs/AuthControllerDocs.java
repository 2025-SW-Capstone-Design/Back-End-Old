package soon.capstone.domain.auth.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import soon.capstone.domain.auth.controller.dto.ReissueTokenRequest;
import soon.capstone.global.security.jwt.dto.response.TokenResponse;
import soon.capstone.global.swagger.annotation.ApiExceptions;

import static soon.capstone.global.exception.dto.ErrorDetail.*;

@Tag(name = "Auth API", description = "인증 관련 API")
public interface AuthControllerDocs {

    @Operation(
        summary = "토큰 재발급",
        description = """
            **사용 목적**:
            - 만료된 액세스 토큰을 재발급받기 위한 API입니다.
            
            **요청 방법**:
            - HTTP `GET` 메서드 사용
            - 요청 URL: `/api/v1/auth/reissue`
            
            **주요 사항**:
            - accessToken과 RefreshToken을 모두 요청 본문에 포함해야 합니다.
            """
    )
    @RequestBody(
        description = "토큰 재발급 요청 정보",
        required = true,
        content = @Content(
            schema = @Schema(implementation = ReissueTokenRequest.class)
        )
    )
    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
        content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    @ApiExceptions({
        INVALID_TOKEN,
        TOKEN_NOT_FOUND,
        MEMBER_NOT_FOUND
    })
    ResponseEntity<TokenResponse> reissueToken(ReissueTokenRequest reissueTokenRequest);
}

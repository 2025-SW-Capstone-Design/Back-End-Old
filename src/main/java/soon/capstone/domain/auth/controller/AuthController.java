package soon.capstone.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.auth.controller.dto.ReissueTokenRequest;
import soon.capstone.domain.auth.service.AuthService;
import soon.capstone.domain.auth.service.dto.request.ReissueTokenServiceRequest;
import soon.capstone.global.security.jwt.dto.response.TokenResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissueToken(
        @RequestBody @Valid ReissueTokenRequest reissueTokenRequest
    ) {
        ReissueTokenServiceRequest serviceRequest = reissueTokenRequest.toServiceRequest();
        TokenResponse tokenResponse = authService.reissueToken(serviceRequest);

        return ResponseEntity.ok(tokenResponse);
    }

}
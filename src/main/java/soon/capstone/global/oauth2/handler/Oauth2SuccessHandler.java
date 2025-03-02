package soon.capstone.global.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import soon.capstone.global.domain.token.dto.response.TokenResponse;
import soon.capstone.global.domain.token.provider.JWTProvider;
import soon.capstone.global.oauth2.dto.CustomOAuth2Member;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@Component
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        CustomOAuth2Member oAuth2Member = (CustomOAuth2Member) authentication.getPrincipal();
        TokenResponse tokenResponse = jwtProvider.generateAllToken(oAuth2Member.getNickname());
        // TODO: redis refresh token 저장

        responseToken(response, tokenResponse);

        log.info("OAuth2 인증 성공 - nickname: {}, 토큰 발행 완료", oAuth2Member.getNickname());
    }

    private void responseToken(HttpServletResponse response, TokenResponse tokenResponse) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        String json = objectMapper.writeValueAsString(tokenResponse);
        response.getWriter().write(json);
        response.getWriter().flush();
    }

}
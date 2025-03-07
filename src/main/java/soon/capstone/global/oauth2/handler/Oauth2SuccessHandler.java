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
import soon.capstone.global.redis.domain.jwt.entity.JWTRefreshToken;
import soon.capstone.global.redis.domain.jwt.repository.JwtRepository;
import soon.capstone.global.redis.domain.oauth2.entity.OauthToken;
import soon.capstone.global.redis.domain.oauth2.repository.OauthTokenRepository;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@Component
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final OauthTokenRepository oauthTokenRepository;
    private final JwtRepository jwtRepository;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        CustomOAuth2Member oAuth2Member = (CustomOAuth2Member) authentication.getPrincipal();
        TokenResponse tokenResponse = jwtProvider.generateAllToken(oAuth2Member.getNickname());

        saveToOauthTokenWithRedis(oAuth2Member);
        saveToRefreshTokenWithRedis(oAuth2Member.getMemberId(), tokenResponse.refreshToken());

        responseToken(response, tokenResponse);
        log.info("OAuth2 인증 성공 - nickname: {}, 토큰 발행 완료", oAuth2Member.getNickname());
    }

    private void saveToRefreshTokenWithRedis(Long memberId, String refreshToken) {
        JWTRefreshToken token = JWTRefreshToken.builder()
            .memberId(memberId)
            .token(refreshToken)
            .build();
        jwtRepository.save(token);
    }

    private void saveToOauthTokenWithRedis(CustomOAuth2Member oAuth2Member) {
        OauthToken token = OauthToken.builder()
            .token(oAuth2Member.getOauth2Token())
            .memberId(oAuth2Member.getMemberId())
            .build();
        oauthTokenRepository.save(token);
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
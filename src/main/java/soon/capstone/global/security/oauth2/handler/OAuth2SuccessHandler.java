package soon.capstone.global.security.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import soon.capstone.global.security.jwt.dto.response.TokenResponse;
import soon.capstone.global.security.jwt.provider.JwtProvider;
import soon.capstone.global.security.oauth2.dto.CustomOAuth2Member;
import soon.capstone.infrastructure.redis.jwt.entity.JwtRefreshToken;
import soon.capstone.infrastructure.redis.jwt.repository.JwtRepository;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final String BASE_URL = "https://planhub.site";
    private final JwtProvider jwtProvider;
    private final OAuthTokenRepository oauthTokenRepository;
    private final JwtRepository jwtRepository;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        CustomOAuth2Member oAuth2Member = (CustomOAuth2Member) authentication.getPrincipal();
        TokenResponse tokenResponse = jwtProvider.generateAllToken(oAuth2Member.getMemberId());

        saveToOauthTokenWithRedis(oAuth2Member);
        saveToRefreshTokenWithRedis(oAuth2Member.getMemberId(), tokenResponse.refreshToken());

        String redirectUrl = buildRedirectUrl(tokenResponse);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        log.info("OAuth2 인증 성공 - nickname: {}, 토큰 발행 완료{}", oAuth2Member.getNickname(), oAuth2Member.getOauthAccessToken());
    }

    private void saveToOauthTokenWithRedis(CustomOAuth2Member oAuth2Member) {
        OAuthToken token = OAuthToken.builder()
            .token(oAuth2Member.getOauthAccessToken())
            .memberId(oAuth2Member.getMemberId())
            .build();
        oauthTokenRepository.save(token);
    }

    private void saveToRefreshTokenWithRedis(Long memberId, String refreshToken) {
        JwtRefreshToken token = JwtRefreshToken.builder()
            .memberId(memberId)
            .token(refreshToken)
            .build();
        jwtRepository.save(token);
    }

    private String buildRedirectUrl(TokenResponse tokenResponse) {
        return UriComponentsBuilder.fromUriString(BASE_URL)
            .queryParam("accessToken", tokenResponse.accessToken())
            .queryParam("refreshToken", tokenResponse.refreshToken())
            .build()
            .toUriString();
    }

}
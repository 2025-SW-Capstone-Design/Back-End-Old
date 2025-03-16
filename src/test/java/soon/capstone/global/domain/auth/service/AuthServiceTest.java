package soon.capstone.global.domain.auth.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.global.domain.auth.service.dto.request.ReissueTokenServiceRequest;
import soon.capstone.global.domain.token.dto.response.TokenResponse;
import soon.capstone.global.domain.token.provider.JwtProvider;
import soon.capstone.global.exception.dto.ErrorDetail;
import soon.capstone.global.exception.token.InvalidRefreshTokenException;
import soon.capstone.global.redis.domain.jwt.entity.JwtRefreshToken;
import soon.capstone.global.redis.domain.jwt.repository.JwtRepository;

import static org.assertj.core.api.Assertions.*;

class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtRepository jwtRepository;

    @AfterEach
    void tearDown() {
        jwtRepository.deleteAll();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("사용자 정보와 기존 토큰을 받아 새로운 액세스 토큰과 리프레시 토큰을 발급한다.")
    @Test
    void reissueToken() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        TokenResponse tokenResponse = jwtProvider.generateAllToken(member.getId());
        ReissueTokenServiceRequest request = ReissueTokenServiceRequest.builder()
            .accessToken(tokenResponse.accessToken())
            .refreshToken(tokenResponse.refreshToken())
            .build();

        JwtRefreshToken jwtRefreshToken = createJwtRefreshToken(request, member.getId());
        jwtRepository.save(jwtRefreshToken);

        // when
        TokenResponse response = authService.reissueToken(request);

        // then
        assertThat(response)
            .extracting("accessToken", "refreshToken")
            .isNotEqualTo(
                tuple(tokenResponse.accessToken(), tokenResponse.refreshToken())
            );
    }

    @DisplayName("레디스내에 존재하지 않는 토큰일 경우 예외가 발생한다.")
    @Test
    void reissueTokenWithNoSaved() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        TokenResponse tokenResponse = jwtProvider.generateAllToken(member.getId());
        ReissueTokenServiceRequest request = ReissueTokenServiceRequest.builder()
            .accessToken(tokenResponse.accessToken())
            .refreshToken(tokenResponse.refreshToken())
            .build();

        // expect
        assertThatThrownBy(() -> authService.reissueToken(request))
            .isInstanceOf(InvalidRefreshTokenException.class)
            .hasMessage(ErrorDetail.INVALID_TOKEN.getMessage());
    }

    private JwtRefreshToken createJwtRefreshToken(ReissueTokenServiceRequest request, Long memberId) {
        return JwtRefreshToken.builder()
            .token(request.refreshToken())
            .memberId(memberId)
            .build();
    }

    private Member createMember() {
        return Member.builder()
            .email("email")
            .nickname("nickname")
            .profileImageURL("profileImageURL")
            .build();
    }

}
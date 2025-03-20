package soon.capstone.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.auth.service.dto.request.ReissueTokenServiceRequest;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.global.exception.token.InvalidRefreshTokenException;
import soon.capstone.global.security.jwt.dto.response.TokenResponse;
import soon.capstone.global.security.jwt.provider.JwtProvider;
import soon.capstone.infrastructure.redis.jwt.entity.JwtRefreshToken;
import soon.capstone.infrastructure.redis.jwt.repository.JwtRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final JwtRepository jwtRepository;

    @Transactional(readOnly = true)
    public TokenResponse reissueToken(ReissueTokenServiceRequest request) {
        String accessToken = request.accessToken();
        Long memberId = Long.valueOf(jwtProvider.getSubjectFromToken(accessToken));
        Member member = memberRepository.findById(memberId);

        validateRefreshToken(member.getId());

        return updateRefreshToken(member);
    }

    private void validateRefreshToken(Long memberId) {
        if (!jwtRepository.existsByMemberId(memberId)) {
            log.warn("Redis에 존재하지 않는 리프레시 토큰입니다. memberId {}", memberId);
            throw new InvalidRefreshTokenException();
        }
    }

    private TokenResponse updateRefreshToken(Member member) {
        JwtRefreshToken byRefreshToken = jwtRepository.findByMemberId(member.getId());
        jwtRepository.delete(byRefreshToken);

        TokenResponse tokenResponse = jwtProvider.generateAllToken(member.getId());

        JwtRefreshToken jwtRefreshToken = JwtRefreshToken.builder()
            .memberId(member.getId())
            .token(tokenResponse.refreshToken())
            .build();
        jwtRepository.save(jwtRefreshToken);

        return tokenResponse;
    }

}
package soon.capstone.infrastructure.redis.jwt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.global.exception.token.TokenNotFoundException;
import soon.capstone.infrastructure.redis.jwt.entity.JwtRefreshToken;

@RequiredArgsConstructor
@Repository
public class JwtRepository {

    private final JwtRedisRepository jwtRedisRepository;

    public void save(JwtRefreshToken jwtRefreshToken) {
        jwtRedisRepository.save(jwtRefreshToken);
    }

    public boolean existsByRefreshToken(String refreshToken) {
        return jwtRedisRepository.existsByToken(refreshToken);
    }

    public JwtRefreshToken findByMemberId(Long memberId) {
        return jwtRedisRepository.findByMemberId(memberId)
            .orElseThrow(TokenNotFoundException::new);
    }

    public void delete(JwtRefreshToken jwtRefreshToken) {
        jwtRedisRepository.delete(jwtRefreshToken);
    }

    public void deleteAll() {
        jwtRedisRepository.deleteAll();
    }

}
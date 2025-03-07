package soon.capstone.global.redis.domain.jwt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.global.redis.domain.jwt.entity.JwtRefreshToken;

@RequiredArgsConstructor
@Repository
public class JwtRepository {

    private final JwtRedisRepository jwtRedisRepository;

    public void save(JwtRefreshToken jwtRefreshToken) {
        jwtRedisRepository.save(jwtRefreshToken);
    }

}
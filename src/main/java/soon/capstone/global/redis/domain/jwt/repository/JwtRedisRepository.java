package soon.capstone.global.redis.domain.jwt.repository;

import org.springframework.data.repository.CrudRepository;
import soon.capstone.global.redis.domain.jwt.entity.JwtRefreshToken;

import java.util.Optional;

public interface JwtRedisRepository extends CrudRepository<JwtRefreshToken, Long> {

    boolean existsByToken(String token);

    Optional<JwtRefreshToken> findByMemberId(Long memberId);

}
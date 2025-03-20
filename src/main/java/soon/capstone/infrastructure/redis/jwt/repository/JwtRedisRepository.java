package soon.capstone.infrastructure.redis.jwt.repository;

import org.springframework.data.repository.CrudRepository;
import soon.capstone.infrastructure.redis.jwt.entity.JwtRefreshToken;

import java.util.Optional;

public interface JwtRedisRepository extends CrudRepository<JwtRefreshToken, Long> {

    boolean existsByToken(String token);

    Optional<JwtRefreshToken> findByMemberId(Long memberId);

}
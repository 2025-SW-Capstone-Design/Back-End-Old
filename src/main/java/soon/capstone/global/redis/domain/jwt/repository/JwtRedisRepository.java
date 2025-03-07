package soon.capstone.global.redis.domain.jwt.repository;

import org.springframework.data.repository.CrudRepository;
import soon.capstone.global.redis.domain.jwt.entity.JWTRefreshToken;

public interface JwtRedisRepository extends CrudRepository<JWTRefreshToken, Long> {
}
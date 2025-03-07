package soon.capstone.global.redis.domain.jwt.repository;

import org.springframework.data.repository.CrudRepository;
import soon.capstone.global.redis.domain.jwt.entity.JwtRefreshToken;

public interface JwtRedisRepository extends CrudRepository<JwtRefreshToken, Long> {
}
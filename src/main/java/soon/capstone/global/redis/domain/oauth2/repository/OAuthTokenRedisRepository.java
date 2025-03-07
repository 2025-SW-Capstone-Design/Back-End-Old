package soon.capstone.global.redis.domain.oauth2.repository;

import org.springframework.data.repository.CrudRepository;
import soon.capstone.global.redis.domain.oauth2.entity.OAuthToken;

public interface OAuthTokenRedisRepository extends CrudRepository<OAuthToken, Long> {
}
package soon.capstone.infrastructure.redis.oauth2.repository;

import org.springframework.data.repository.CrudRepository;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;

public interface OAuthTokenRedisRepository extends CrudRepository<OAuthToken, Long> {
}
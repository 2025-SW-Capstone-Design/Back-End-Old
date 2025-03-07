package soon.capstone.global.redis.domain.oauth2.repository;

import org.springframework.data.repository.CrudRepository;
import soon.capstone.global.redis.domain.oauth2.entity.OauthToken;

public interface OauthTokenRedisRepository extends CrudRepository<OauthToken, Long> {
}
package soon.capstone.infrastructure.redis.oauth2.repository;

import org.springframework.data.repository.CrudRepository;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;

import java.util.Optional;

public interface OAuthTokenRedisRepository extends CrudRepository<OAuthToken, Long> {

    Optional<OAuthToken> findByMemberId(Long memberId);

}
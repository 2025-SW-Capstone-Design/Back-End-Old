package soon.capstone.global.redis.domain.oauth2.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.global.redis.domain.oauth2.entity.OAuthToken;

@RequiredArgsConstructor
@Repository
public class OAuthTokenRepository {

    private final OAuthTokenRedisRepository oauthTokenRedisRepository;

    public void save(OAuthToken oauthAccessToken) {
        oauthTokenRedisRepository.save(oauthAccessToken);
    }

}
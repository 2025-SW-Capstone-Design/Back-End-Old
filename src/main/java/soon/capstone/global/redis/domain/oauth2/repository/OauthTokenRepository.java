package soon.capstone.global.redis.domain.oauth2.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.global.redis.domain.oauth2.entity.OauthToken;

@RequiredArgsConstructor
@Repository
public class OauthTokenRepository {

    private final OauthTokenRedisRepository oauthTokenRedisRepository;

    public void save(OauthToken oauthToken) {
        oauthTokenRedisRepository.save(oauthToken);
    }

}
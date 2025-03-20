package soon.capstone.infrastructure.redis.oauth2.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.global.exception.token.TokenNotFoundException;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;

@RequiredArgsConstructor
@Repository
public class OAuthTokenRepository {

    private final OAuthTokenRedisRepository oauthTokenRedisRepository;

    public void save(OAuthToken oauthAccessToken) {
        oauthTokenRedisRepository.save(oauthAccessToken);
    }

    public OAuthToken findByMemberId(Long memberId) {
        return oauthTokenRedisRepository.findByMemberId(memberId)
            .orElseThrow(TokenNotFoundException::new);
    }

}
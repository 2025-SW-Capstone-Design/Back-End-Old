package soon.capstone.global.redis.domain.oauth2.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "oauth_token")
public class OAuthToken {

    @Id
    private Long memberId;

    private String token;

    @TimeToLive(unit = TimeUnit.HOURS)
    private long expiration;

    @Builder
    private OAuthToken(Long memberId, String token) {
        this.memberId = memberId;
        this.token = token;
        this.expiration = 8;
    }

}
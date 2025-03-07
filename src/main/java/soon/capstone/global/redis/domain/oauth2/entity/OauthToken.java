package soon.capstone.global.redis.domain.oauth2.entity;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "oauth_token")
public class OauthToken {

    @Id
    private Long memberId;

    @Indexed
    private String token;

    @TimeToLive(unit = TimeUnit.HOURS)
    private long expiration;

    @Builder
    private OauthToken(Long memberId, String token) {
        this.memberId = memberId;
        this.token = token;
        this.expiration = 8;
    }

}
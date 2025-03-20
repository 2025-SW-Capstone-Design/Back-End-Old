package soon.capstone.infrastructure.redis.jwt.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import static soon.capstone.global.security.jwt.common.TokenExpiration.REFRESH_TOKEN;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "jwt_refresh_token")
public class JwtRefreshToken {

    @Id
    private Long memberId;

    @Indexed
    private String token;

    @TimeToLive
    private long expiration;

    @Builder
    private JwtRefreshToken(Long memberId, String token) {
        this.memberId = memberId;
        this.token = token;
        this.expiration = REFRESH_TOKEN.getExpirationTime() / 1000; // 밀리 초 -> 초
    }

}
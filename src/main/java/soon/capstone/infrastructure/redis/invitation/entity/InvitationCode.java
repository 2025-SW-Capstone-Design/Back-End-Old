package soon.capstone.infrastructure.redis.invitation.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "invitation_code")
public class InvitationCode {

    @Id
    private String id;

    @Indexed
    private Long teamId;

    private String code;

    @TimeToLive(unit = TimeUnit.DAYS)
    private long expiration;

    @Builder
    private InvitationCode(Long teamId, String code) {
        this.id = "team" + teamId;
        this.teamId = teamId;
        this.code = code;
        this.expiration = 1;
    }

}
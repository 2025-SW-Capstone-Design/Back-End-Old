package soon.capstone.infrastructure.redis.openvidu.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "temporaryRoomIdentity", timeToLive = 60 * 60 * 24) // 1일
public class TemporaryRoomIdentity {

    @Id
    private String sid;

    private String identity;

    @Builder
    private TemporaryRoomIdentity(String identity, String sid) {
        this.identity = identity;
        this.sid = sid;
    }

    public static TemporaryRoomIdentity create(Long memberId, Long teamId, String sid) {
        return TemporaryRoomIdentity.builder()
            .identity(memberId + ":" + teamId)
            .sid(sid)
            .build();
    }

}
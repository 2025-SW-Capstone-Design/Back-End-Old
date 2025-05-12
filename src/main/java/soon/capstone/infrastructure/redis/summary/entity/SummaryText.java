package soon.capstone.infrastructure.redis.summary.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "summary_text")
public class SummaryText {

    @Id
    private String id;

    @Indexed
    private Long chatRoomId;

    private int index;

    private String summary;

    @Builder
    private SummaryText(Long chatRoomId, int index, String summary) {
        this.id = chatRoomId + ":" + index;
        this.chatRoomId = chatRoomId;
        this.index = index;
        this.summary = summary;
    }

}
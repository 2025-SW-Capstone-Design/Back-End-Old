package soon.capstone.infrastructure.redis.summary.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import soon.capstone.infrastructure.redis.summary.entity.SummaryText;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class SummaryTextRepository {

    private static final String INDEX_KEY_PREFIX = "summary_text:index:";

    private final SummaryTextRedisRepository summaryTextRedisRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public SummaryText save(Long chatRoomId, String summaryText) {
        SummaryText summary = SummaryText.builder()
            .chatRoomId(chatRoomId)
            .index(getNextIndex(chatRoomId))
            .summary(summaryText)
            .build();

        return summaryTextRedisRepository.save(summary);
    }

    public void resetIndex(Long chatRoomId) {
        String key = INDEX_KEY_PREFIX + chatRoomId;
        redisTemplate.delete(key);
    }

    public List<SummaryText> findAllByChatRoomId(Long chatRoomId) {
        return summaryTextRedisRepository.findAllByChatRoomIdOrderByIndexAsc(chatRoomId);
    }

    public void deleteAll() {
        summaryTextRedisRepository.deleteAll();
    }

    private int getNextIndex(Long chatRoomId) {
        String key = INDEX_KEY_PREFIX + chatRoomId;
        Long index = redisTemplate.opsForValue().increment(key);
        return index != null ? index.intValue() : 1;
    }

}
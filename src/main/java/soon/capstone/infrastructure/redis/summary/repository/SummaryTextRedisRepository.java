package soon.capstone.infrastructure.redis.summary.repository;

import org.springframework.data.repository.CrudRepository;
import soon.capstone.infrastructure.redis.summary.entity.SummaryText;

import java.util.List;

public interface SummaryTextRedisRepository extends CrudRepository<SummaryText, String> {

    List<SummaryText> findAllByChatRoomIdOrderByIndexAsc(Long chatRoomId);

}
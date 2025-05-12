package soon.capstone.infrastructure.redis.summary.repository;

import org.springframework.data.repository.CrudRepository;
import soon.capstone.infrastructure.redis.summary.entity.SummaryText;

public interface SummaryTextRedisRepository extends CrudRepository<SummaryText, String> {

}
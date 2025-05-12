package soon.capstone.infrastructure.redis.summary.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class SummaryTextRepository {

    private final SummaryTextRedisRepository summaryTextRedisRepository;

}
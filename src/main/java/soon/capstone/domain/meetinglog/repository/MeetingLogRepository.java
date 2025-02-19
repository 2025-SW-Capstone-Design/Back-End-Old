package soon.capstone.domain.meetinglog.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MeetingLogRepository {

    private final MeetingLogJpaRepository meetingLogJpaRepository;

}
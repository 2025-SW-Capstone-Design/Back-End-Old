package soon.capstone.domain.meetinglog.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.meetinglog.entity.MeetingLog;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class MeetingLogRepository {

    private final MeetingLogJpaRepository meetingLogJpaRepository;

    public void save(MeetingLog meetingLog) {
        meetingLogJpaRepository.save(meetingLog);
    }

    public List<MeetingLog> findAll() {
        return meetingLogJpaRepository.findAll();
    }

    public void deleteAllInBatch() {
        meetingLogJpaRepository.deleteAllInBatch();
    }

}
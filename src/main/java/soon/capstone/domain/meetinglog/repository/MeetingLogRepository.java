package soon.capstone.domain.meetinglog.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.meetinglog.entity.MeetingLog;
import soon.capstone.global.exception.meetinglog.MeetingLogNotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class MeetingLogRepository {

    private final MeetingLogJpaRepository meetingLogJpaRepository;

    public void save(MeetingLog meetingLog) {
        meetingLogJpaRepository.save(meetingLog);
    }

    public void saveAll(List<MeetingLog> meetingLog) {
        meetingLogJpaRepository.saveAll(meetingLog);
    }

    public List<MeetingLog> findAll() {
        return meetingLogJpaRepository.findAll();
    }

    public MeetingLog findById(Long id) {
        return meetingLogJpaRepository.findById(id)
            .orElseThrow(MeetingLogNotFoundException::new);
    }

    public void deleteAllInBatch() {
        meetingLogJpaRepository.deleteAllInBatch();
    }

    public List<MeetingLog> findAllByTeamIdOrderByCreateTimeDesc(Long teamId) {
        return meetingLogJpaRepository.findAllByTeamIdOrderByCreateTimeDesc(teamId);
    }

}
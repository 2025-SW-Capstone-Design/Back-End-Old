package soon.capstone.domain.meetinglog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.meetinglog.entity.MeetingLog;

public interface MeetingLogJpaRepository extends JpaRepository<MeetingLog, Long> {

}
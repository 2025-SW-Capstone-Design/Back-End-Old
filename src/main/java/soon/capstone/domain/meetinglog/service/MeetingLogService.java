package soon.capstone.domain.meetinglog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.meetinglog.repository.MeetingLogRepository;

@RequiredArgsConstructor
@Service
public class MeetingLogService {

    private final MeetingLogRepository meetingLogRepository;

}
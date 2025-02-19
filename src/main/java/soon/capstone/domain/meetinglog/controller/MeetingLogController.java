package soon.capstone.domain.meetinglog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.meetinglog.service.MeetingLogService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/meeting-logs")
@RestController
public class MeetingLogController {

    private final MeetingLogService meetingLogService;

}
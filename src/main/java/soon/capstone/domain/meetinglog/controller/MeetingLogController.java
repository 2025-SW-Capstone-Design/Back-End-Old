package soon.capstone.domain.meetinglog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.meetinglog.controller.dto.MeetingLogUpdateRequest;
import soon.capstone.domain.meetinglog.service.MeetingLogService;
import soon.capstone.domain.meetinglog.service.dto.response.MeetingLogDetailResponse;
import soon.capstone.global.anootation.AuthMemberId;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/{teamId}/meeting-logs")
@RestController
public class MeetingLogController {

    private final MeetingLogService meetingLogService;

    @PatchMapping("/{meetingLogId}")
    public ResponseEntity<Void> updateMeetingLog(
        @Valid @RequestBody MeetingLogUpdateRequest request,
        @AuthMemberId Long memberId,
        @PathVariable Long meetingLogId,
        @PathVariable Long teamId
    ) {
        meetingLogService.update(request.toServiceRequest(teamId, memberId, meetingLogId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{meetingLogId}")
    public ResponseEntity<MeetingLogDetailResponse> getMeetingLogDetail(
        @PathVariable Long meetingLogId
    ) {
        MeetingLogDetailResponse response = meetingLogService.getMeetingLogDetail(meetingLogId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MeetingLogDetailResponse>> getMeetingLogsByTeamId(
        @PathVariable Long teamId
    ) {
        List<MeetingLogDetailResponse> response = meetingLogService.getMeetingLogsByTeamId(teamId);
        return ResponseEntity.ok(response);
    }

}
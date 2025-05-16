package soon.capstone.domain.meetinglog.service.dto.response;

import lombok.Builder;
import soon.capstone.domain.meetinglog.entity.MeetingLog;

import java.time.LocalDate;

@Builder
public record MeetingLogDetailResponse(

    Long id,
    String title,
    String content,
    LocalDate createdAt

) {

    public static MeetingLogDetailResponse from(MeetingLog meetingLog) {
        return MeetingLogDetailResponse.builder()
            .id(meetingLog.getId())
            .title(meetingLog.getTitle())
            .content(meetingLog.getContent())
            .createdAt(meetingLog.getCreateTime().toLocalDate())
            .build();
    }

}
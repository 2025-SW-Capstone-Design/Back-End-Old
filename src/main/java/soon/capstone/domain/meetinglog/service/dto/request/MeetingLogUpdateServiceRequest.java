package soon.capstone.domain.meetinglog.service.dto.request;

import lombok.Builder;

@Builder
public record MeetingLogUpdateServiceRequest(

    Long id,
    String title,
    String content

) {
}
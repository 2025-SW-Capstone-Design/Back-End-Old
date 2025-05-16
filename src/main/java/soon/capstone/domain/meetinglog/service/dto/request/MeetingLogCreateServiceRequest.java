package soon.capstone.domain.meetinglog.service.dto.request;

import lombok.Builder;

@Builder
public record MeetingLogCreateServiceRequest(

    String content,
    Long memberId,
    Long teamId

) {
}
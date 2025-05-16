package soon.capstone.domain.meetinglog.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import soon.capstone.domain.meetinglog.service.dto.request.MeetingLogUpdateServiceRequest;

@Builder
public record MeetingLogUpdateRequest(

    @NotBlank(message = "제목은 필수입니다.")
    String title,

    @NotBlank(message = "내용은 필수입니다.")
    String content

) {

    public MeetingLogUpdateServiceRequest toServiceRequest(Long teamId, Long memberId, Long meetingLogId) {
        return MeetingLogUpdateServiceRequest.builder()
            .title(title)
            .content(content)
            .memberId(memberId)
            .teamId(teamId)
            .id(meetingLogId)
            .build();
    }

}
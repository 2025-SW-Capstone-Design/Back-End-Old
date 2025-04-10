package soon.capstone.domain.readme.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import soon.capstone.domain.readme.service.dto.request.ReadmeUpdateServiceRequest;

@Builder
public record ReadmeUpdateRequest(

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    String title,

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    String content

) {

    public ReadmeUpdateServiceRequest toServiceRequest(Long memberId, Long readmeId, Long projectId, Long teamId) {
        return ReadmeUpdateServiceRequest.builder()
            .title(title)
            .content(content)
            .memberId(memberId)
            .readmeId(readmeId)
            .projectId(projectId)
            .teamId(teamId)
            .build();
    }

}
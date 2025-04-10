package soon.capstone.domain.readme.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import soon.capstone.domain.readme.service.dto.request.ReadmeCreateServiceRequest;

@Builder
public record ReadmeCreateRequest(

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    String title,

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    String content

) {

    public ReadmeCreateServiceRequest toServiceRequest(Long memberId, Long projectId, Long teamId) {
        return ReadmeCreateServiceRequest.builder()
            .title(title)
            .content(content)
            .projectId(projectId)
            .teamId(teamId)
            .memberId(memberId)
            .build();
    }

}
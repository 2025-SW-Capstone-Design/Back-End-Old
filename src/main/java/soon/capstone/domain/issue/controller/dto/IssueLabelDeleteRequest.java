package soon.capstone.domain.issue.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import soon.capstone.domain.issue.service.dto.request.IssueLabelDeleteServiceRequest;

@Builder
public record IssueLabelDeleteRequest(

    @NotBlank(message = "라벨명은 필수 입력 값입니다.")
    String title,

    @NotBlank(message = "오가니제이션명은 필수 입력 값입니다.")
    String organizationName,

    @NotBlank(message = "리포지토리명은 필수 입력 값입니다.")
    String repositoryName

) {

    public IssueLabelDeleteServiceRequest toServiceRequest(Long labelId, Long teamId) {
        return IssueLabelDeleteServiceRequest.builder()
            .labelId(labelId)
            .teamId(teamId)
            .title(title)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .build();
    }

}
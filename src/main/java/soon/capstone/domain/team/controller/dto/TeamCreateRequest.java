package soon.capstone.domain.team.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import soon.capstone.domain.team.service.dto.request.TeamCreateServiceRequest;

@Builder
public record TeamCreateRequest(

    @NotBlank(message = "팀 이름을 입력해주세요")
    String name,

    @NotBlank(message = "팀 설명을 입력해주세요")
    String description,

    @NotBlank(message = "오가니제이션명을 입력해주세요")
    String organizationName

) {

    public TeamCreateServiceRequest toServiceRequest() {
        return TeamCreateServiceRequest.builder()
            .name(name)
            .description(description)
            .organizationName(organizationName)
            .build();
    }

}
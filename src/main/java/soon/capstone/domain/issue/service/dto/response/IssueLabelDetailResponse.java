package soon.capstone.domain.issue.service.dto.response;

import lombok.Builder;

@Builder
public record IssueLabelDetailResponse(

    Long id,
    String name,
    String color,
    String description

) {
}
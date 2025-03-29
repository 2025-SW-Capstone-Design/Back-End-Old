package soon.capstone.domain.project.service.dto.response;

import lombok.Builder;

@Builder
public record ProjectDetailResponse(
        Long projectId,
        String title,
        String creator
) {
}

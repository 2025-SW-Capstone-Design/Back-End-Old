package soon.capstone.domain.readme.service.dto.request;

import lombok.Builder;

@Builder
public record ReadmeCreateServiceRequest(

    String title,
    String content,
    Long projectId,
    Long teamId,
    Long memberId

) {
}
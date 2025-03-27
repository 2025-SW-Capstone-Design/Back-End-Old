package soon.capstone.domain.project.service.dto.request;

import lombok.Builder;

@Builder
public record TeamCreatedEvent(
        Long teamId,
        Long memberId,
        String oauthToken
) {
}


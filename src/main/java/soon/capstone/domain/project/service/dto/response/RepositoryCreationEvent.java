package soon.capstone.domain.project.service.dto.response;

import lombok.Builder;

@Builder
public record RepositoryCreationEvent(
    String oauthToken,
    String organizationName
) {
}

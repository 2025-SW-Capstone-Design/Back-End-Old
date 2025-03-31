package soon.capstone.domain.issue.service.dto.request;

import lombok.Builder;

@Builder
public record IssueLabelUpdateServiceRequest(

    Long labelId,
    String oldTitle,
    String newTitle,
    String description,
    String color,
    String organizationName,
    String repositoryName,
    Long projectId,
    Long teamId

) {
}
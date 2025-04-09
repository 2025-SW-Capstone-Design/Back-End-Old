package soon.capstone.domain.issue.service.dto.request;

import lombok.Builder;

@Builder
public record IssueLabelDeleteServiceRequest(

    Long labelId,
    Long teamId,
    String title,
    String organizationName,
    String repositoryName

) {

}
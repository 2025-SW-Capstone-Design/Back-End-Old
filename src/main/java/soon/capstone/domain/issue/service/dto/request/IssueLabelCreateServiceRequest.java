package soon.capstone.domain.issue.service.dto.request;

import lombok.Builder;

@Builder
public record IssueLabelCreateServiceRequest(

    String title,
    String description,
    String color,
    Long projectId,
    Long teamId

) {

}
package soon.capstone.domain.team.service.dto.request;

import lombok.Builder;

@Builder
public record TeamCreateServiceRequest(

    String name,
    String description,
    String organizationName

) {

}
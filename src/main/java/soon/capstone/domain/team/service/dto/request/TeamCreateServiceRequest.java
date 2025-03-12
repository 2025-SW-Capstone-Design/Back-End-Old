package soon.capstone.domain.team.service.dto.request;

import lombok.Builder;
import soon.capstone.domain.team.entity.Team;

@Builder
public record TeamCreateServiceRequest(

    String name,
    String description,
    String organizationName

) {

    public Team toEntity() {
        return Team.builder()
            .name(name)
            .description(description)
            .organizationName(organizationName)
            .build();
    }

}
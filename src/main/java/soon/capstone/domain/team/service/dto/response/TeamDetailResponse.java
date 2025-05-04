package soon.capstone.domain.team.service.dto.response;

import lombok.Builder;
import soon.capstone.domain.team.entity.Team;

@Builder
public record TeamDetailResponse(

    Long id,
    String name,
    String description,
    String organizationName

) {

    public static TeamDetailResponse from(Team team) {
        return TeamDetailResponse.builder()
            .id(team.getId())
            .name(team.getName())
            .description(team.getDescription())
            .organizationName(team.getOrganizationName())
            .build();
    }

}
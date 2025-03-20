package soon.capstone.external.github.dto;

import lombok.Builder;

@Builder
public record GithubOrganizationMembershipDto(

    String role, // admin | member
    String state // active | pending

) {

    public boolean isAdminWithActiveStatus() {
        return "admin".equals(role) && "active".equals(state);
    }

}
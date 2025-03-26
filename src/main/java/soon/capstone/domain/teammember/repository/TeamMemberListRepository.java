package soon.capstone.domain.teammember.repository;

import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;

import java.util.List;

public interface TeamMemberListRepository {

    List<TeamMemberDetailResponse> getTeamMembers(Team team);

}
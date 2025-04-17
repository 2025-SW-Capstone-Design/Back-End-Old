package soon.capstone.domain.milestone.repository;

import soon.capstone.domain.milestone.service.dto.MilestoneMailDto;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.team.entity.Team;

import java.util.List;

public interface MilestoneListRepository {
    List<MilestoneResponse> getMilestonesByProject(Project project);
    List<MilestoneResponse> getMilestonesByTeam(Team team);
    List<MilestoneMailDto> getEmailsByMilestones();
}

package soon.capstone.domain.milestone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.team.entity.Team;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MilestoneReadService {

    private final MilestoneRepository milestoneRepository;

    public List<MilestoneResponse> getMilestonesByProject(Project project) {
        return milestoneRepository.getMilestonesByProject(project);
    }

    public List<MilestoneResponse> getMilestonesByTeam(Team team) {
        return milestoneRepository.getMilestonesByTeam(team);
    }
}

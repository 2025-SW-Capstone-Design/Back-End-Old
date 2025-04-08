package soon.capstone.domain.milestone.repository;

import soon.capstone.domain.milestone.controller.dto.response.MilestoneResponse;
import soon.capstone.domain.project.entity.Project;

import java.util.List;

public interface MilestoneListRepository {
    List<MilestoneResponse> getMilestonesByProject(Project project);
}

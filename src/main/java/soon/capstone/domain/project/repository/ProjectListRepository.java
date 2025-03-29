package soon.capstone.domain.project.repository;

import soon.capstone.domain.project.service.dto.response.ProjectDetailResponse;
import soon.capstone.domain.team.entity.Team;

import java.util.List;

public interface ProjectListRepository {
    List<ProjectDetailResponse> getProjects(Team team);
}

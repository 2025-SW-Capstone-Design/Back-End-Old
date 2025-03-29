package soon.capstone.domain.project.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.service.dto.response.ProjectDetailResponse;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.global.exception.project.ProjectNotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ProjectRepository {

    private final ProjectJpaRepository projectJpaRepository;

    public void save(Project project) {
        projectJpaRepository.save(project);
    }

    public List<Project> findAll() {
        return projectJpaRepository.findAll();
    }

    public Project findById(Long id) {
        return projectJpaRepository.findById(id).orElseThrow(ProjectNotFoundException::new);
    }

    public List<ProjectDetailResponse> getProjects(Team team) {
        return projectJpaRepository.getProjects(team);
    }

    public void deleteAllInBatch() {
        projectJpaRepository.deleteAllInBatch();
    }
}
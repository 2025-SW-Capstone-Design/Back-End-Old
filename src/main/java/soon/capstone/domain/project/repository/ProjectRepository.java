package soon.capstone.domain.project.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.project.entity.Project;

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
        return projectJpaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

}
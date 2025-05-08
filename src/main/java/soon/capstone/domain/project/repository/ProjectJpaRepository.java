package soon.capstone.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.project.entity.Project;

import java.util.List;

public interface ProjectJpaRepository extends JpaRepository<Project, Long>, ProjectListRepository {

    List<Project> findAllByTeamIdAndCreator(Long teamId, String creator);

}
package soon.capstone.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.project.entity.Project;

import java.util.Optional;

public interface ProjectJpaRepository extends JpaRepository<Project, Long>, ProjectListRepository {

    Optional<Project> findByTeamIdAndCreator(Long teamId, String creator);

}
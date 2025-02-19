package soon.capstone.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.project.entity.Project;

public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

}
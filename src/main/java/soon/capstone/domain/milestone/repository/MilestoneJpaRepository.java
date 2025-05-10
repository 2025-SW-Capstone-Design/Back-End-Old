package soon.capstone.domain.milestone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.project.entity.Project;

public interface MilestoneJpaRepository extends
    JpaRepository<Milestone, Long>,
    MilestoneListRepository,
    MilestoneCustomRepository {

    boolean existsByTitleAndProject(String title, Project project);

}
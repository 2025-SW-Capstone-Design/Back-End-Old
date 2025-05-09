package soon.capstone.domain.milestone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.milestone.entity.Milestone;

public interface MilestoneJpaRepository extends
    JpaRepository<Milestone, Long>,
    MilestoneListRepository,
    MilestoneCustomRepository {

    boolean existsByTitle(String title);

}
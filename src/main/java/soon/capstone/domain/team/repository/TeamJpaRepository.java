package soon.capstone.domain.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.team.entity.Team;

public interface TeamJpaRepository extends JpaRepository<Team, Long> {

}
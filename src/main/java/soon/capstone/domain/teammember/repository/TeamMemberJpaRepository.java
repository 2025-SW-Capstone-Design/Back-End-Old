package soon.capstone.domain.teammember.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.teammember.entity.TeamMember;

public interface TeamMemberJpaRepository extends JpaRepository<TeamMember, Long> {

}
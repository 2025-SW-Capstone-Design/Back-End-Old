package soon.capstone.domain.teammember.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.entity.common.Role;

import java.util.Optional;

public interface TeamMemberJpaRepository extends JpaRepository<TeamMember, Long> {

    boolean existsByMemberAndTeam(Member member, Team team);

    Optional<TeamMember> findByMemberId(Long memberId);

    Optional<TeamMember> findByTeamId(Long teamId);

    Optional<TeamMember> findByTeamIdAndRole(Long teamId, Role role);

}
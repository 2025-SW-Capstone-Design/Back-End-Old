package soon.capstone.domain.teammember.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.entity.common.Role;

import java.util.List;
import java.util.Optional;

public interface TeamMemberJpaRepository extends
    JpaRepository<TeamMember, Long>,
    TeamMemberListRepository {

    boolean existsByMemberAndTeam(Member member, Team team);

    boolean existsByTeamIdAndMemberId(Long teamId, Long memberId);

    Optional<TeamMember> findByMemberId(Long memberId);

    Optional<TeamMember> findByTeamId(Long teamId);

    Optional<TeamMember> findByTeamIdAndRole(Long teamId, Role role);

    Optional<TeamMember> findByTeamIdAndMemberId(Long teamId, Long memberId);

    List<TeamMember> findAllByMemberId(Long memberId);

}
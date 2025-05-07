package soon.capstone.domain.teammember.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;
import soon.capstone.global.exception.team.TeamNotFoundException;
import soon.capstone.global.exception.teammember.TeamMemberNotFoundException;

import java.util.List;

import static soon.capstone.domain.teammember.entity.common.Role.ROLE_LEADER;

@RequiredArgsConstructor
@Repository
public class TeamMemberRepository {

    private final TeamMemberJpaRepository teamMemberJpaRepository;

    public void save(TeamMember teamMember) {
        teamMemberJpaRepository.save(teamMember);
    }

    public void saveAll(List<TeamMember> teamMembers) {
        teamMemberJpaRepository.saveAll(teamMembers);
    }

    public TeamMember findById(Long teamMemberId) {
        return teamMemberJpaRepository.findById(teamMemberId)
            .orElseThrow(TeamMemberNotFoundException::new);
    }

    public TeamMember findByMemberId(Long memberId) {
        return teamMemberJpaRepository.findByMemberId(memberId)
            .orElseThrow(TeamMemberNotFoundException::new);
    }

    public List<TeamMember> findAllByMemberId(Long memberId) {
        return teamMemberJpaRepository.findAllByMemberId(memberId);
    }

    public TeamMember findByTeamId(Long teamId) {
        return teamMemberJpaRepository.findByTeamId(teamId)
            .orElseThrow(TeamNotFoundException::new);
    }

    public TeamMember findByTeamIdAndLeader(Long teamId) {
        return teamMemberJpaRepository.findByTeamIdAndRole(teamId, ROLE_LEADER)
            .orElseThrow(TeamMemberNotFoundException::new);
    }

    public TeamMember findByTeamIdAndMemberId(Long teamId, Long memberId) {
        return teamMemberJpaRepository.findByTeamIdAndMemberId(teamId, memberId)
            .orElseThrow(TeamMemberNotFoundException::new);
    }

    public boolean existsByMemberAndTeam(Member member, Team team) {
        return teamMemberJpaRepository.existsByMemberAndTeam(member, team);
    }

    public List<TeamMemberDetailResponse> getTeamMembers(Team team) {
        return teamMemberJpaRepository.getTeamMembers(team);
    }

    public void deleteAllInBatch() {
        teamMemberJpaRepository.deleteAllInBatch();
    }

    public boolean existsByTeamIdAndMemberId(Long teamId, Long memberId) {
        return teamMemberJpaRepository.existsByTeamIdAndMemberId(teamId, memberId);
    }

}
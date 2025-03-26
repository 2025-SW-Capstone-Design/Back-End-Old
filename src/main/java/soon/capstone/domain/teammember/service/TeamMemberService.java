package soon.capstone.domain.teammember.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.entity.common.Role;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.domain.teammember.service.dto.request.TeamMemberUpdateRoleServiceRequest;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;
import soon.capstone.global.exception.team.TeamNotAuthorizedException;

import java.util.List;

import static soon.capstone.domain.teammember.entity.common.Role.isLeader;

@RequiredArgsConstructor
@Service
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    public List<TeamMemberDetailResponse> getTeamMembers(Long teamId, Long memberId) {
        Team team = teamRepository.findById(teamId);
        Member member = memberRepository.findById(memberId);

        boolean existsByMemberAndTeam = teamMemberRepository.existsByMemberAndTeam(member, team);
        if (!existsByMemberAndTeam) {
            throw new TeamNotAuthorizedException();
        }

        return teamMemberRepository.getTeamMembers(team);
    }

    @Transactional
    public void updateTeamMemberRole(TeamMemberUpdateRoleServiceRequest request, Long memberId) {
        TeamMember requester = teamMemberRepository.findByTeamIdAndMemberId(request.teamId(), memberId);

        if (!isLeader(requester.getRole())) {
            throw new TeamNotAuthorizedException();
        }

        TeamMember targetMember = teamMemberRepository.findByTeamIdAndMemberId(request.teamId(), request.teamMemberId());
        Role role = Role.contains(request.role());
        targetMember.updateRole(role);
    }

}
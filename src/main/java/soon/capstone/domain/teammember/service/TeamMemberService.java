package soon.capstone.domain.teammember.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.domain.teammember.service.dto.request.TeamMemberDetailServiceRequest;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;
import soon.capstone.global.exception.team.TeamNotAuthorizedException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    public List<TeamMemberDetailResponse> getTeamMembers(TeamMemberDetailServiceRequest request, Long memberId) {
        Team team = teamRepository.findById(request.teamId());
        Member member = memberRepository.findById(memberId);

        boolean existsByMemberAndTeam = teamMemberRepository.existsByMemberAndTeam(member, team);
        if (!existsByMemberAndTeam) {
            throw new TeamNotAuthorizedException();
        }

        return teamMemberRepository.getTeamMembers(team);
    }

}
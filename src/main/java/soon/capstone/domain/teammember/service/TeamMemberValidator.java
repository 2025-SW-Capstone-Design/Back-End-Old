package soon.capstone.domain.teammember.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.team.TeamNotAuthorizedException;

@RequiredArgsConstructor
@Service
public class TeamMemberValidator {
    private final TeamMemberRepository teamMemberRepository;

    public void validateTeamMember(Member member, Team team) {
        if (!teamMemberRepository.existsByMemberAndTeam(member, team)) {
            throw new TeamNotAuthorizedException();
        }
    }
}

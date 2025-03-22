package soon.capstone.domain.team.service.team;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.github.OAuthTokenExpiredException;
import soon.capstone.global.exception.teammember.AlreadyTeamMemberException;
import soon.capstone.infrastructure.github.service.GithubOrganizationService;
import soon.capstone.infrastructure.redis.invitation.entity.InvitationCode;
import soon.capstone.infrastructure.redis.invitation.repository.InvitationCodeRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamJoinService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InvitationCodeRepository invitationCodeRepository;
    private final GithubOrganizationService githubOrganizationService;

    @Transactional
    public Long joinTeamWithInvitationCode(Member member, String invitationCode) {
        InvitationCode code = invitationCodeRepository.findByCode(invitationCode);
        Team team = teamRepository.findById(code.getTeamId());

        boolean alreadyMember = teamMemberRepository.existsByMemberAndTeam(member, team);
        if (alreadyMember) {
            throw new AlreadyTeamMemberException();
        }

        Long leaderId = teamMemberRepository.findByTeamIdAndLeader(team.getId())
            .getMember()
            .getId();

        githubOrganizationService.addMemberToOrganization(
            leaderId,
            member.getNickname(),
            team.getOrganizationName()
        );

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        return team.getId();
    }

}
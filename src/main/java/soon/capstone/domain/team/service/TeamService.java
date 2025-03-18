package soon.capstone.domain.team.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.team.service.dto.request.TeamCreateServiceRequest;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.external.github.service.GithubOrganizationService;
import soon.capstone.global.exception.team.IsNotAdminInOrganizationException;
import soon.capstone.global.exception.team.IsNotTeamLeaderException;
import soon.capstone.global.exception.team.TeamAlreadyExistsException;
import soon.capstone.global.redis.domain.oauth2.entity.OAuthToken;
import soon.capstone.global.redis.domain.oauth2.repository.OAuthTokenRepository;

import static soon.capstone.domain.teammember.entity.common.Role.isLeader;

@RequiredArgsConstructor
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;
    private final OAuthTokenRepository oAuthTokenRepository;
    private final GithubOrganizationService githubOrganizationService;
    private final InvitationCodeGenerator invitationCodeGenerator;

    @Transactional
    public Long createTeam(TeamCreateServiceRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId);
        OAuthToken oAuthToken = oAuthTokenRepository.findByMemberId(memberId);

        validateTeamCreation(request, oAuthToken);

        Team team = request.toEntity();
        teamRepository.save(team);

        TeamMember leader = TeamMember.createLeader(member, team);
        teamMemberRepository.save(leader);

        return team.getId();
    }

    @Transactional
    public String generateInvitationCode(Long teamId, Long memberId) {
        TeamMember teamMember = teamMemberRepository.findByMemberId(memberId);

        if (!isLeader(teamMember.getRole())) {
            throw new IsNotTeamLeaderException();
        }

        return invitationCodeGenerator.generateInvitationCode(teamId);
    }

    private void validateTeamCreation(TeamCreateServiceRequest request, OAuthToken oAuthToken) {
        boolean existsByNameOrOrganizationName = teamRepository.existsByNameOrOrganizationName(request.name(), request.organizationName());
        if (existsByNameOrOrganizationName) {
            throw new TeamAlreadyExistsException();
        }

        boolean adminInOrganization = githubOrganizationService.isAdminInOrganization(oAuthToken.getToken(), request.organizationName());
        if (!adminInOrganization) {
            throw new IsNotAdminInOrganizationException();
        }
    }

}
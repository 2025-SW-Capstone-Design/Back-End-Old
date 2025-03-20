package soon.capstone.domain.team.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.team.service.dto.request.TeamCreateServiceRequest;
import soon.capstone.domain.team.service.dto.request.TeamGenerateInvitationCodeServiceRequest;
import soon.capstone.domain.team.service.dto.request.TeamInvitationServiceRequest;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.infrastructure.github.service.GithubOrganizationService;
import soon.capstone.global.email.service.EmailSendService;
import soon.capstone.global.exception.team.IsNotAdminInOrganizationException;
import soon.capstone.global.exception.team.IsNotTeamLeaderException;
import soon.capstone.global.exception.team.TeamAlreadyExistsException;
import soon.capstone.infrastructure.redis.invitation.repository.InvitationCodeRepository;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;

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
    private final InvitationCodeRepository invitationCodeRepository;
    private final EmailSendService emailSendService;

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

    @Transactional(readOnly = true)
    public String generateInvitationCode(TeamGenerateInvitationCodeServiceRequest request, Long memberId) {
        // TODO: 이미 코드가 존재하는 경우 그대로 반환하는 로직 추가
        TeamMember teamMember = teamMemberRepository.findByMemberId(memberId);

        if (!isLeader(teamMember.getRole())) {
            throw new IsNotTeamLeaderException();
        }

        return invitationCodeGenerator.generateInvitationCode(request.teamId());
    }

    @Transactional(readOnly = true)
    public void sendInvitationEmails(TeamInvitationServiceRequest request, Long memberId) {
        TeamMember teamMember = teamMemberRepository.findByMemberId(memberId);
        if (!isLeader(teamMember.getRole())) {
            throw new IsNotTeamLeaderException();
        }

        String invitationCode = invitationCodeRepository.findByTeamId(request.teamId()).getCode();
        request.emails().forEach(email -> {
            emailSendService.sendInvitationCodeEmail(email, invitationCode);
        });
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
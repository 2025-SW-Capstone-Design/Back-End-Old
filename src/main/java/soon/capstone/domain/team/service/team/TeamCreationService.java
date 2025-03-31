package soon.capstone.domain.team.service.team;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.project.service.dto.request.TeamCreatedEvent;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.team.IsNotAdminInOrganizationException;
import soon.capstone.global.exception.team.TeamAlreadyExistsException;
import soon.capstone.infrastructure.github.service.organization.GithubOrganizationService;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;

@RequiredArgsConstructor
@Service
public class TeamCreationService {

    private final OAuthTokenRepository oAuthTokenRepository;
    private final GithubOrganizationService githubOrganizationService;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long createTeam(
        String name,
        String organizationName,
        String description,
        Member member
    ) {
        OAuthToken oAuthToken = oAuthTokenRepository.findByMemberId(member.getId());
        validateTeamCreation(name, organizationName, oAuthToken);

        Team team = Team.builder()
            .organizationName(organizationName)
            .name(name)
            .description(description)
            .build();
        teamRepository.save(team);

        TeamMember leader = TeamMember.createLeader(member, team);
        teamMemberRepository.save(leader);

        eventPublisher.publishEvent(
                TeamCreatedEvent.builder()
                        .teamId(team.getId())
                        .memberId(member.getId())
                        .oauthToken(oAuthToken.getToken())
                        .build()
        );

        return team.getId();
    }

    private void validateTeamCreation(String name, String organizationName, OAuthToken oAuthToken) {
        boolean existsByNameOrOrganizationName = teamRepository.existsByNameOrOrganizationName(name, organizationName);
        if (existsByNameOrOrganizationName) {
            throw new TeamAlreadyExistsException();
        }

        boolean adminInOrganization = githubOrganizationService.isAdminInOrganization(oAuthToken.getToken(), organizationName);
        if (!adminInOrganization) {
            throw new IsNotAdminInOrganizationException();
        }
    }

}
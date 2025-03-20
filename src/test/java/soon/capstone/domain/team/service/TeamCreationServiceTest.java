package soon.capstone.domain.team.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.infrastructure.github.service.GithubOrganizationService;
import soon.capstone.global.exception.team.IsNotAdminInOrganizationException;
import soon.capstone.global.exception.team.TeamAlreadyExistsException;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static soon.capstone.domain.teammember.entity.common.Position.NONE;
import static soon.capstone.domain.teammember.entity.common.Role.ROLE_LEADER;
import static soon.capstone.global.exception.dto.ErrorDetail.*;

class TeamCreationServiceTest extends IntegrationTestSupport {

    @Autowired
    private TeamCreationService teamCreationService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private OAuthTokenRepository oAuthTokenRepository;

    @MockitoBean
    private GithubOrganizationService githubOrganizationService;

    @AfterEach
    void tearDown() {
        teamMemberRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("팀 생성을 요청한 멤버가 조직 관리자인 경우 팀이 생성된다")
    @Test
    void createTeamWhenMemberIsAdminInOrganization() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        OAuthToken oAuthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oAuthToken);

        Team team = createTeam();

        given(githubOrganizationService.isAdminInOrganization(oAuthToken.getToken(), team.getOrganizationName()))
            .willReturn(true);

        // when
        Long teamId = teamCreationService.createTeam(
            team.getName(),
            team.getOrganizationName(),
            team.getDescription(),
            member
        );

        // then
        Team foundTeam = teamRepository.findById(teamId);
        assertThat(foundTeam)
            .extracting("name", "description", "organizationName")
            .containsExactly("name", "description", "organizationName");
    }

    @DisplayName("팀 생성을 요청한 멤버가 조직의 관리자가 아닌 경우 예외가 발생한다")
    @Test
    void createTeamWhenMemberIsNotAdminInOrganization() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        OAuthToken oAuthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oAuthToken);

        Team team = createTeam();

        given(githubOrganizationService.isAdminInOrganization(oAuthToken.getToken(), team.getOrganizationName()))
            .willReturn(false);

        // expected
        assertThatThrownBy(() -> teamCreationService.createTeam(
            team.getName(),
            team.getOrganizationName(),
            team.getDescription(),
            member
        ))
            .isInstanceOf(IsNotAdminInOrganizationException.class)
            .hasMessage(IS_NOT_ADMIN_IN_ORGANIZATION.getMessage());
    }

    @DisplayName("팀을 생성 한 경우 요청한 멤버의 권한은 LEADER로 설정된다")
    @Test
    void createTeamShouldSetMemberRoleToLeader() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        OAuthToken oAuthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oAuthToken);

        Team team = createTeam();

        given(githubOrganizationService.isAdminInOrganization(oAuthToken.getToken(), team.getOrganizationName()))
            .willReturn(true);

        // when
        Long teamId = teamCreationService.createTeam(
            team.getName(),
            team.getOrganizationName(),
            team.getDescription(),
            member
        );

        // then
        TeamMember teamMember = teamMemberRepository.findByTeamId(teamId);
        assertThat(teamMember.getRole())
            .isEqualTo(ROLE_LEADER);
    }

    @DisplayName("팀을 생성 한 경우 요청한 멤버의 포지션은 NONE(초기상태)로 지정된다")
    @Test
    void createTeamShouldSetMemberPositionToNone() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        OAuthToken oAuthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oAuthToken);

        Team team = createTeam();

        given(githubOrganizationService.isAdminInOrganization(oAuthToken.getToken(), team.getOrganizationName()))
            .willReturn(true);

        // when
        Long teamId = teamCreationService.createTeam(
            team.getName(),
            team.getOrganizationName(),
            team.getDescription(),
            member
        );

        // then
        TeamMember teamMember = teamMemberRepository.findByTeamId(teamId);
        assertThat(teamMember.getPosition())
            .isEqualTo(NONE);
    }

    @DisplayName("중복된 팀 이름으로 팀을 생성할 경우 예외가 발생한다")
    @Test
    void cannotCreateTeamWithDuplicateNameInSameOrganization() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        OAuthToken oAuthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oAuthToken);

        Team team = createTeam();

        given(githubOrganizationService.isAdminInOrganization(oAuthToken.getToken(), team.getOrganizationName()))
            .willReturn(true);

        teamCreationService.createTeam(
            team.getName(),
            team.getOrganizationName(),
            team.getDescription(),
            member
        );

        Team duplicateTeam = createTeam();

        // expected
        assertThatThrownBy(() -> teamCreationService.createTeam(
            duplicateTeam.getName(),
            duplicateTeam.getOrganizationName(),
            duplicateTeam.getDescription(),
            member
        ))
            .isInstanceOf(TeamAlreadyExistsException.class)
            .hasMessage(TEAM_ALREADY_EXISTS.getMessage());
    }

    private Member createMember() {
        return Member.builder()
            .email("email")
            .nickname("nickname")
            .profileImageURL("profileImageURL")
            .build();
    }

    private Team createTeam() {
        return Team.builder()
            .name("name")
            .description("description")
            .organizationName("organizationName")
            .build();
    }

    private OAuthToken createOAuthToken(Member member) {
        return OAuthToken.builder()
            .memberId(member.getId())
            .token("token")
            .build();
    }

}
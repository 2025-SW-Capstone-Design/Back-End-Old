package soon.capstone.domain.team.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.team.service.dto.request.TeamCreateServiceRequest;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.external.github.service.GithubOrganizationService;
import soon.capstone.global.email.service.EmailSendService;
import soon.capstone.global.exception.team.IsNotAdminInOrganizationException;
import soon.capstone.global.exception.team.IsNotTeamLeaderException;
import soon.capstone.global.exception.team.TeamAlreadyExistsException;
import soon.capstone.global.redis.domain.invitation.entity.InvitationCode;
import soon.capstone.global.redis.domain.invitation.repository.InvitationCodeRepository;
import soon.capstone.global.redis.domain.oauth2.entity.OAuthToken;
import soon.capstone.global.redis.domain.oauth2.repository.OAuthTokenRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static soon.capstone.domain.teammember.entity.common.Position.NONE;
import static soon.capstone.domain.teammember.entity.common.Role.ROLE_LEADER;
import static soon.capstone.domain.teammember.entity.common.Role.ROLE_MEMBER;
import static soon.capstone.global.exception.dto.ErrorDetail.*;

class TeamServiceTest extends IntegrationTestSupport {

    @Autowired
    private TeamService teamService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private OAuthTokenRepository oAuthTokenRepository;

    @Autowired
    private InvitationCodeRepository invitationCodeRepository;

    @MockitoBean
    private GithubOrganizationService githubOrganizationService;

    @MockitoBean
    private InvitationCodeGenerator invitationCodeGenerator;

    @MockitoBean
    private EmailSendService emailSendService;

    @AfterEach
    void tearDown() {
        teamMemberRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        invitationCodeRepository.deleteAll();
    }

    @DisplayName("팀 생성을 요청한 멤버가 조직 관리자인 경우 팀이 생성된다")
    @Test
    void createTeamWhenMemberIsAdminInOrganization() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        OAuthToken oAuthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oAuthToken);

        TeamCreateServiceRequest request = createTeamServiceRequest();

        given(githubOrganizationService.isAdminInOrganization(oAuthToken.getToken(), request.organizationName()))
            .willReturn(true);

        // when
        Long teamId = teamService.createTeam(request, member.getId());

        // then
        Team team = teamRepository.findById(teamId);

        assertThat(team)
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

        TeamCreateServiceRequest request = createTeamServiceRequest();

        given(githubOrganizationService.isAdminInOrganization(oAuthToken.getToken(), request.organizationName()))
            .willReturn(false);

        // expected
        assertThatThrownBy(() -> teamService.createTeam(request, member.getId()))
            .isInstanceOf(IsNotAdminInOrganizationException.class)
            .hasMessage(IS_NOT_ADMIN_IN_ORGANIZATION.getMessage());
    }

    @DisplayName("팀을 생성 한 경우 요청한 멤버의 권한은 LEADER로 설정된다.")
    @Test
    void createTeamShouldSetMemberRoleToLeader() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        OAuthToken oAuthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oAuthToken);

        TeamCreateServiceRequest request = createTeamServiceRequest();

        given(githubOrganizationService.isAdminInOrganization(oAuthToken.getToken(), request.organizationName()))
            .willReturn(true);

        // when
        Long teamId = teamService.createTeam(request, member.getId());

        // then
        TeamMember teamMember = teamMemberRepository.findByTeamId(teamId);
        assertThat(teamMember.getRole())
            .isEqualTo(ROLE_LEADER);
    }

    @DisplayName("팀을 생성 한 경우 요청한 멤버의 포지션은 NONE(초기상태)로 지정된다.")
    @Test
    void createTeamShouldSetMemberPositionToNone() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        OAuthToken oAuthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oAuthToken);

        TeamCreateServiceRequest request = createTeamServiceRequest();

        given(githubOrganizationService.isAdminInOrganization(oAuthToken.getToken(), request.organizationName()))
            .willReturn(true);

        // when
        Long teamId = teamService.createTeam(request, member.getId());

        // then
        TeamMember teamMember = teamMemberRepository.findByTeamId(teamId);
        assertThat(teamMember.getPosition())
            .isEqualTo(NONE);
    }

    @DisplayName("중복된 팀 이름으로 팀을 생성할 경우 예외가 발생한다.")
    @Test
    void cannotCreateTeamWithDuplicateNameInSameOrganization() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        OAuthToken oAuthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oAuthToken);

        TeamCreateServiceRequest request = createTeamServiceRequest();

        given(githubOrganizationService.isAdminInOrganization(oAuthToken.getToken(), request.organizationName()))
            .willReturn(true);

        teamService.createTeam(request, member.getId());

        // expected
        assertThatThrownBy(() -> teamService.createTeam(request, member.getId()))
            .isInstanceOf(TeamAlreadyExistsException.class)
            .hasMessage(TEAM_ALREADY_EXISTS.getMessage());
    }

    @DisplayName("팀 초대 코드를 생성한다")
    @Test
    void generateInvitationCode() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createLeader(member, team);
        teamMemberRepository.save(teamMember);

        String fixedCode = "ABCD123";
        given(invitationCodeGenerator.generateInvitationCode(team.getId()))
            .willReturn(fixedCode);

        // when
        String invitationCode = teamService.generateInvitationCode(team.getId(), member.getId());

        // then
        assertThat(invitationCode)
            .isEqualTo(fixedCode);
    }

    @DisplayName("팀 리더가 아닌 경우 초대 코드 생성시 예외를 발생한다.")
    @Test
    void generateInvitationCodeIsNotLeader() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.builder()
            .role(ROLE_MEMBER)
            .member(member)
            .team(team)
            .position(NONE)
            .build();
        teamMemberRepository.save(teamMember);

        // expected
        assertThatThrownBy(() -> teamService.generateInvitationCode(team.getId(), member.getId()))
            .isInstanceOf(IsNotTeamLeaderException.class)
            .hasMessage(IS_NOT_TEAM_LEADER.getMessage());
    }

    @DisplayName("입력된 이메일들에 초대 코드를 전송한다.")
    @Test
    void sendInvitationEmails() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createLeader(member, team);
        teamMemberRepository.save(teamMember);

        String fixedCode = "ABCD123";
        List<String> emails = List.of("test1@example.com", "test2@example.com");

        InvitationCode invitationCode = createInvitationCode(team, fixedCode);
        invitationCodeRepository.save(invitationCode);

        // when
        teamService.sendInvitationEmails(team.getId(), member.getId(), emails);

        // then
        then(emailSendService)
            .should(times(2))
            .sendInvitationCodeEmail(anyString(), eq(fixedCode));
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

    private TeamCreateServiceRequest createTeamServiceRequest() {
        return TeamCreateServiceRequest.builder()
            .description("description")
            .name("name")
            .organizationName("organizationName")
            .build();
    }

    private InvitationCode createInvitationCode(Team team, String fixedCode) {
        return InvitationCode.builder()
            .teamId(team.getId())
            .code(fixedCode)
            .build();
    }

}
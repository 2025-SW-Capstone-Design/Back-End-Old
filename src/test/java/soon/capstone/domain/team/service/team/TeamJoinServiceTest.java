package soon.capstone.domain.team.service.team;

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
import soon.capstone.global.exception.dto.ErrorDetail;
import soon.capstone.global.exception.github.OAuthTokenExpiredException;
import soon.capstone.global.exception.team.InvitationCodeNotFoundException;
import soon.capstone.global.exception.teammember.AlreadyTeamMemberException;
import soon.capstone.infrastructure.github.service.GithubOrganizationService;
import soon.capstone.infrastructure.redis.invitation.entity.InvitationCode;
import soon.capstone.infrastructure.redis.invitation.repository.InvitationCodeRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static soon.capstone.global.exception.dto.ErrorDetail.*;

class TeamJoinServiceTest extends IntegrationTestSupport {

    @Autowired
    private TeamJoinService teamJoinService;

    @Autowired
    private InvitationCodeRepository invitationCodeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @MockitoBean
    private GithubOrganizationService githubOrganizationService;

    @AfterEach
    void tearDown() {
        teamMemberRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        invitationCodeRepository.deleteAll();
    }

    @DisplayName("초대코드로 팀 가입에 성공한다.")
    @Test
    void joinTeamWithInvitationCode() {
        // given
        Member leader = createMember("leaderEmail", "leaderNickname");
        memberRepository.save(leader);
        Member member = createMember("memberEmail", "memberNickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamLeader = TeamMember.createLeader(leader, team);
        teamMemberRepository.save(teamLeader);

        InvitationCode invitationCode = createInvitationCode(team);
        invitationCodeRepository.save(invitationCode);

        // when
        Long teamId = teamJoinService.joinTeamWithInvitationCode(member, invitationCode.getCode());

        // then
        assertThat(teamId)
            .isEqualTo(team.getId());

        then(githubOrganizationService)
            .should(times(1))
            .addMemberToOrganization(
                leader.getId(),
                member.getNickname(),
                team.getOrganizationName()
            );
    }

    @DisplayName("OAuthToken이 만료된 경우 예외가 발생한다.")
    @Test
    void joinTeamWithInvitationCodeWithoutOAuthTokenExpired() {
        // given
        Member leader = createMember("leaderEmail", "leaderNickname");
        memberRepository.save(leader);
        Member member = createMember("memberEmail", "memberNickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamLeader = TeamMember.createLeader(leader, team);
        teamMemberRepository.save(teamLeader);

        InvitationCode invitationCode = createInvitationCode(team);
        invitationCodeRepository.save(invitationCode);

        willThrow(new OAuthTokenExpiredException())
            .given(githubOrganizationService)
            .addMemberToOrganization(
                leader.getId(),
                member.getNickname(),
                team.getOrganizationName()
            );

        // expected
        assertThatThrownBy(() -> teamJoinService.joinTeamWithInvitationCode(member, invitationCode.getCode()))
            .isInstanceOf(OAuthTokenExpiredException.class)
            .hasMessage(OAUTH_TOKEN_EXPIRED.getMessage());
    }

    @DisplayName("존재하지 않는 초대 코드로 가입 시도 시 예외가 발생한다")
    @Test
    void joinTeamWithInvalidInvitationCode() {
        // given
        Member leader = createMember("leaderEmail", "leaderNickname");
        memberRepository.save(leader);
        Member member = createMember("memberEmail", "memberNickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamLeader = TeamMember.createLeader(leader, team);
        teamMemberRepository.save(teamLeader);

        InvitationCode invitationCode = createInvitationCode(team);
        invitationCodeRepository.save(invitationCode);

        // expected
        assertThatThrownBy(() -> teamJoinService.joinTeamWithInvitationCode(member, "invalidCode"))
            .isInstanceOf(InvitationCodeNotFoundException.class)
            .hasMessage(INVITATION_CODE_NOT_FOUND.getMessage());
    }

    @DisplayName("이미 팀에 가입된 멤버가 가입 시도 시 예외가 발생한다")
    @Test
    void joinTeamWithAlreadyJoinedMember() {
        // given
        Member leader = createMember("leaderEmail", "leaderNickname");
        memberRepository.save(leader);
        Member member = createMember("memberEmail", "memberNickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamLeader = TeamMember.createLeader(leader, team);
        teamMemberRepository.save(teamLeader);
        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        InvitationCode invitationCode = createInvitationCode(team);
        invitationCodeRepository.save(invitationCode);

        // expected
        assertThatThrownBy(() -> teamJoinService.joinTeamWithInvitationCode(member, invitationCode.getCode()))
            .isInstanceOf(AlreadyTeamMemberException.class)
            .hasMessage(TEAM_MEMBER_ALREADY_EXISTS.getMessage());
    }

    private Member createMember(String email, String nickname) {
        return Member.builder()
            .email(email)
            .nickname(nickname)
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

    private InvitationCode createInvitationCode(Team team) {
        return InvitationCode.builder()
            .teamId(team.getId())
            .code("code")
            .build();
    }

}
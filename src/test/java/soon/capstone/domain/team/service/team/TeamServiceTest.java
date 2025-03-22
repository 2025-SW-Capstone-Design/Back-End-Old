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
import soon.capstone.domain.team.service.dto.request.TeamCreateServiceRequest;
import soon.capstone.domain.team.service.dto.request.TeamGenerateInvitationCodeServiceRequest;
import soon.capstone.domain.team.service.dto.request.TeamInvitationServiceRequest;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.team.IsNotTeamLeaderException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static soon.capstone.domain.teammember.entity.common.Position.NONE;
import static soon.capstone.domain.teammember.entity.common.Role.ROLE_MEMBER;
import static soon.capstone.global.exception.dto.ErrorDetail.IS_NOT_TEAM_LEADER;

class TeamServiceTest extends IntegrationTestSupport {

    @Autowired
    private TeamService teamService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @MockitoBean
    private TeamInvitationService teamInvitationService;

    @MockitoBean
    private TeamCreationService teamCreationService;

    @AfterEach
    void tearDown() {
        teamMemberRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("팀이 생성된다")
    @Test
    void createTeamSuccess() {
        // given
        Long teamId = 1L;

        Member member = createMember();
        memberRepository.save(member);

        var request = TeamCreateServiceRequest.builder()
            .name("name")
            .organizationName("organizationName")
            .description("description")
            .build();

        given(teamCreationService.createTeam(anyString(), anyString(), anyString(), any()))
            .willReturn(teamId);

        // when
        Long createdTeamId = teamService.createTeam(request, member.getId());

        // then
        assertThat(createdTeamId)
            .isEqualTo(teamId);
    }

    @DisplayName("팀의 리더인 멤버가 초대 코드 생성을 요청하면 성공한다")
    @Test
    void generateInvitationCodeSuccessWhenLeader() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leaderMember = TeamMember.createLeader(member, team);
        teamMemberRepository.save(leaderMember);

        String expectedCode = "ABCD123";
        var request = TeamGenerateInvitationCodeServiceRequest.builder()
            .teamId(team.getId())
            .build();

        given(teamInvitationService.generateInvitationCode(team.getId()))
            .willReturn(expectedCode);

        // when
        String result = teamService.generateInvitationCode(request, member.getId());

        // then
        assertThat(result)
            .isEqualTo(expectedCode);
    }

    @DisplayName("팀의 리더가 아닌 멤버가 초대 코드 생성을 요청하면 예외가 발생한다")
    @Test
    void generateInvitationCodeFailsWhenNotLeader() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        var request = TeamGenerateInvitationCodeServiceRequest.builder()
            .teamId(team.getId())
            .build();

        TeamMember teamMember = TeamMember.builder()
            .role(ROLE_MEMBER)
            .member(member)
            .team(team)
            .position(NONE)
            .build();
        teamMemberRepository.save(teamMember);

        // except
        assertThatThrownBy(() -> teamService.generateInvitationCode(request, member.getId()))
            .isInstanceOf(IsNotTeamLeaderException.class)
            .hasMessage(IS_NOT_TEAM_LEADER.getMessage());
    }

    @DisplayName("팀의 리더인 멤버가 초대 이메일 발송을 요청하면 성공한다")
    @Test
    void sendInvitationEmailsSuccessWhenLeader() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leaderMember = TeamMember.createLeader(member, team);
        teamMemberRepository.save(leaderMember);

        var request = TeamInvitationServiceRequest.builder()
            .emails(List.of("test1@example.com", "test2@example.com"))
            .teamId(team.getId())
            .build();

        // when
        teamService.sendInvitationEmails(request, member.getId());

        // then
        then(teamInvitationService).should()
            .sendInvitationEmails(
                request.teamId(),
                request.emails()
            );
    }

    @DisplayName("팀의 리더가 아닌 멤버가 초대 이메일 발송을 요청하면 예외가 발생한다")
    @Test
    void sendInvitationEmailsFailsWhenNotLeader() {
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

        var request = TeamInvitationServiceRequest.builder()
            .emails(List.of("test1@example.com", "test2@example.com"))
            .teamId(1L)
            .build();

        // except
        assertThatThrownBy(() -> teamService.sendInvitationEmails(request, member.getId()))
            .isInstanceOf(IsNotTeamLeaderException.class)
            .hasMessage(IS_NOT_TEAM_LEADER.getMessage());
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

}
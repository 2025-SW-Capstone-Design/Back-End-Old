package soon.capstone.domain.teammember.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.entity.common.Position;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.domain.teammember.service.dto.request.TeamMemberUpdatePositionServiceRequest;
import soon.capstone.domain.teammember.service.dto.request.TeamMemberUpdateRoleServiceRequest;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;
import soon.capstone.global.exception.common.InvalidRequest;
import soon.capstone.global.exception.team.TeamNotAuthorizedException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static soon.capstone.domain.teammember.entity.common.Role.ROLE_LEADER;
import static soon.capstone.global.exception.dto.ErrorDetail.TEAM_NOT_AUTHORIZED;

class TeamMemberServiceTest extends IntegrationTestSupport {

    @Autowired
    private TeamMemberService teamMemberService;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @AfterEach
    void tearDown() {
        teamMemberRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("팀에 소속된 멤버는 팀 멤버 목록을 조회할 수 있다")
    @Test
    void getTeamMembers() {
        // given
        Member member1 = createMember("email1", "nickname1");
        Member member2 = createMember("email2", "nickname2");
        Member member3 = createMember("email3", "nickname3");
        memberRepository.saveAll(List.of(member1, member2, member3));

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leader = TeamMember.createLeader(member1, team);
        TeamMember teamMember2 = TeamMember.createMember(member2, team);
        TeamMember teamMember3 = TeamMember.createMember(member3, team);
        teamMemberRepository.saveAll(List.of(leader, teamMember2, teamMember3));

        // when
        List<TeamMemberDetailResponse> teamMembers = teamMemberService.getTeamMembers(team.getId(), member1.getId());

        // then
        assertThat(teamMembers).hasSize(3)
            .extracting("memberId", "position", "role", "nickname", "profileImageURL")
            .containsExactlyInAnyOrder(
                tuple(member1.getId(), "NONE", "ROLE_LEADER", "nickname1", "profileImageURL"),
                tuple(member2.getId(), "NONE", "ROLE_MEMBER", "nickname2", "profileImageURL"),
                tuple(member3.getId(), "NONE", "ROLE_MEMBER", "nickname3", "profileImageURL")
            );
    }

    @DisplayName("팀에 소속되지 않은 멤버는 팀 멤버 목록을 조회 할 경우 예외가 발생한다.")
    @Test
    void getTeamMembers_WhenMemberIsNotAuthorized_ThrowsTeamNotAuthorizedException() {
        // given
        Member teamMember = createMember("email1", "nickname1");
        Member nonTeamMember = createMember("email2", "nickname2");
        memberRepository.saveAll(List.of(teamMember, nonTeamMember));

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember member = TeamMember.createMember(teamMember, team);
        teamMemberRepository.save(member);

        // expect
        assertThatThrownBy(() -> teamMemberService.getTeamMembers(team.getId(), nonTeamMember.getId()))
            .isInstanceOf(TeamNotAuthorizedException.class)
            .hasMessage(TEAM_NOT_AUTHORIZED.getMessage());
    }

    @DisplayName("팀 리더는 다른 팀원의 역할을 업데이트 할 수 있다.")
    @Test
    void updateTeamMemberRole() {
        // given
        Member leader = createMember("email1", "nickname1");
        Member member = createMember("email2", "nickname2");
        memberRepository.saveAll(List.of(leader, member));

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leaderMember = TeamMember.createLeader(leader, team);
        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.saveAll(List.of(leaderMember, teamMember));

        var request = new TeamMemberUpdateRoleServiceRequest(
            team.getId(), leader.getId(), member.getId(), ROLE_LEADER.name()
        );

        // when
        teamMemberService.updateTeamMemberRole(request);

        // then
        TeamMember updatedMember = teamMemberRepository.findByTeamIdAndMemberId(team.getId(), member.getId());
        assertThat(updatedMember.getRole())
            .isEqualTo(ROLE_LEADER);
    }

    @DisplayName("팀 리더가 아닌 멤버가 역할을 업데이트 하면 예외가 발생한다.")
    @Test
    void updateTeamMemberRoleIsNotLeader() {
        // given
        Member leader = createMember("email1", "nickname1");
        Member member = createMember("email2", "nickname2");
        memberRepository.saveAll(List.of(leader, member));

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leaderMember = TeamMember.createLeader(leader, team);
        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.saveAll(List.of(leaderMember, teamMember));

        var request = new TeamMemberUpdateRoleServiceRequest(
            team.getId(), member.getId(), leader.getId(), ROLE_LEADER.name()
        );

        // expected
        assertThatThrownBy(() -> teamMemberService.updateTeamMemberRole(request))
            .isInstanceOf(TeamNotAuthorizedException.class)
            .hasMessage(TEAM_NOT_AUTHORIZED.getMessage());
    }

    @DisplayName("존재하지 않는 역할로 업데이트시 예외가 발생한다.")
    @Test
    void updateTeamMemberRoleWithoutInvalidRole() {
        // given
        Member leader = createMember("email1", "nickname1");
        Member member = createMember("email2", "nickname2");
        memberRepository.saveAll(List.of(leader, member));

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leaderMember = TeamMember.createLeader(leader, team);
        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.saveAll(List.of(leaderMember, teamMember));

        var request = new TeamMemberUpdateRoleServiceRequest(
            team.getId(), leader.getId(), member.getId(), "ROLE_INVALID"
        );

        // expected
        assertThatThrownBy(() -> teamMemberService.updateTeamMemberRole(request))
            .isInstanceOf(InvalidRequest.class)
            .hasMessage("잘못된 요청입니다.");
    }

    @DisplayName("팀의 리더는 다른 팀원의 포지션을 변경 할 수 있다")
    @Test
    void updateTeamMemberPosition() {
        // given
        Member leader = createMember("email1", "nickname1");
        Member member = createMember("email2", "nickname2");
        memberRepository.saveAll(List.of(leader, member));

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leaderMember = TeamMember.createLeader(leader, team);
        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.saveAll(List.of(leaderMember, teamMember));

        var request = TeamMemberUpdatePositionServiceRequest.builder()
            .teamId(team.getId())
            .memberId(member.getId())
            .requesterId(leader.getId())
            .position("BACKEND")
            .build();

        // when
        teamMemberService.updateTeamMemberPosition(request);

        // then
        TeamMember updatedMember = teamMemberRepository.findByTeamIdAndMemberId(team.getId(), member.getId());
        assertThat(updatedMember.getPosition())
            .isEqualTo(Position.BACKEND);
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
            .organizationName("organizationName")
            .name("teamName")
            .description("teamDescription")
            .build();
    }

}
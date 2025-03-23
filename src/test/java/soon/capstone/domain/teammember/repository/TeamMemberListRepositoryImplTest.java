package soon.capstone.domain.teammember.repository;

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
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

class TeamMemberListRepositoryImplTest extends IntegrationTestSupport {

    @Autowired
    private TeamMemberListRepositoryImpl teamMemberListRepositoryImpl;

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

    @DisplayName("팀 멤버 목록을 조회한다.")
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
        TeamMember teamMember = TeamMember.createMember(member2, team);
        TeamMember teamMember1 = TeamMember.createMember(member3, team);
        teamMemberRepository.saveAll(List.of(leader, teamMember, teamMember1));

        // when
        List<TeamMemberDetailResponse> teamMembers = teamMemberListRepositoryImpl.getTeamMembers(team);

        // then
        assertThat(teamMembers).hasSize(3)
            .extracting("memberId", "position", "role", "nickname", "profileImageURL")
            .containsExactlyInAnyOrder(
                tuple(member1.getId(), "NONE", "ROLE_LEADER", "nickname1", "profileImageURL"),
                tuple(member2.getId(), "NONE", "ROLE_MEMBER", "nickname2", "profileImageURL"),
                tuple(member3.getId(), "NONE", "ROLE_MEMBER", "nickname3", "profileImageURL")
            );
    }

    @DisplayName("팀에 소속된 멤버가 없을 경우 빈 목록을 반환한다.")
    @Test
    void getTeamMembersReturnsEmptyList() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        // when
        List<TeamMemberDetailResponse> teamMembers = teamMemberListRepositoryImpl.getTeamMembers(team);

        // then
        assertThat(teamMembers)
            .isEmpty();
    }

    @DisplayName("팀에 소속된 멤버가 하나인 경우 올바른 정보를 반환한다.")
    @Test
    void getTeamMembersReturnsSingleMember() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        // when
        List<TeamMemberDetailResponse> teamMembers = teamMemberListRepositoryImpl.getTeamMembers(team);

        // then
        assertThat(teamMembers).hasSize(1)
            .extracting("memberId", "position", "role", "nickname", "profileImageURL")
            .containsExactly(
                tuple(member.getId(), "NONE", "ROLE_MEMBER", "nickname", "profileImageURL")
            );
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


}
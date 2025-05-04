package soon.capstone.domain.team.service.team;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.team.service.dto.response.TeamDetailResponse;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TeamReaderServiceTest extends IntegrationTestSupport {

    @Autowired
    private TeamReaderService teamReaderService;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @AfterEach
    void tearDown() {
        teamMemberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("멤버가 참여한 모든 팀을 조회한다.")
    @Test
    void getTeamDetails() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team1 = createTeam("organizationName1");
        Team team2 = createTeam("organizationName2");
        teamRepository.saveAll(List.of(team1, team2));

        TeamMember leader1 = TeamMember.createLeader(member, team1);
        TeamMember leader2 = TeamMember.createLeader(member, team2);
        teamMemberRepository.saveAll(List.of(leader1, leader2));

        // when
        List<TeamDetailResponse> teamDetails = teamReaderService.getTeamDetails(member.getId());

        // then
        assertThat(teamDetails).hasSize(2)
            .extracting("organizationName")
            .containsExactlyInAnyOrder("organizationName1", "organizationName2");
    }

    @DisplayName("멤버가 참여한 팀이 없는 경우 빈 리스트를 반환한다.")
    @Test
    void getTeamDetailsWithoutEmptyList() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        // when
        List<TeamDetailResponse> teamDetails = teamReaderService.getTeamDetails(member.getId());

        // then
        assertThat(teamDetails).isEmpty();
    }

    private Member createMember(String email, String nickname) {
        return Member.builder()
            .email(email)
            .nickname(nickname)
            .profileImageURL("profileImageURL")
            .build();
    }

    private Team createTeam(String organizationName) {
        return Team.builder()
            .name("name")
            .description("description")
            .organizationName(organizationName)
            .build();
    }

}
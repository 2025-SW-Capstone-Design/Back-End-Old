package soon.capstone.domain.project.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.project.service.dto.response.ProjectDetailResponse;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.entity.common.Position;
import soon.capstone.domain.teammember.entity.common.Role;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProjectReadService projectReadService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @AfterEach
    void tearDown() {
        projectRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("사용자가 속해있는 팀의 프로젝트 목록을 반환한다.")
    @Test
    void getProjects() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = createTeamMember(member, team);
        teamMemberRepository.save(teamMember);

        Project mockProject1 = createProject(member.getNickname(), team);
        Project mockProject2 = createProject(member.getNickname(), team);

        projectRepository.save(mockProject1);
        projectRepository.save(mockProject2);

        // When
        List<ProjectDetailResponse> projects = projectReadService.getProjects(member, team.getId());

        // Then
        assertThat(projects.size()).isEqualTo(2);
        assertThat(projects).extracting("title")
            .containsExactlyInAnyOrder(mockProject1.getTitle(), mockProject2.getTitle());
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

    private TeamMember createTeamMember(Member member, Team team) {
        return TeamMember.builder()
            .member(member)
            .team(team)
            .position(Position.NONE)
            .role(Role.ROLE_MEMBER)
            .build();
    }

    private Project createProject(String creator, Team team) {
        return Project.builder()
                .title("title")
                .repositoryId("repositoryId")
                .creator(creator)
                .team(team)
                .build();
    }
}
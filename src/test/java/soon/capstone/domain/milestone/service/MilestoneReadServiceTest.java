package soon.capstone.domain.milestone.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
class MilestoneReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private MilestoneReadService milestoneReadService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @AfterEach
    void tearDown() {
        milestoneRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("프로젝트에 속한 마일스톤들을 반환한다.")
    @Test
    void getMilestonesByProject() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(member.getNickname(), team);
        projectRepository.save(project);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        milestoneRepository.saveAll(
                List.of(
                        createMilestone(project, startDate, dueDate),
                        createMilestone(project, startDate, dueDate)
                )
        );

        // When
        List<MilestoneResponse> milestones = milestoneReadService.getMilestonesByProject(project);

        // Then
        assertThat(milestones).hasSize(2);
        assertThat(milestones)
                .extracting("title", "description", "creator")
                .containsExactlyInAnyOrder(
                        tuple("Test", "Description", "nickname"),
                        tuple("Test", "Description", "nickname")
                );

    }

    @DisplayName("팀에 속한 마일스톤들을 반환한다.")
    @Test
    void getMilestonesByTeam() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(member.getNickname(), team);
        projectRepository.save(project);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        milestoneRepository.saveAll(
                List.of(
                        createMilestone(project, startDate, dueDate),
                        createMilestone(project, startDate, dueDate)
                )
        );

        // When
        List<MilestoneResponse> milestones = milestoneReadService.getMilestonesByTeam(team);

        // Then
        assertThat(milestones).hasSize(2);
        assertThat(milestones)
                .extracting("title", "description", "creator")
                .containsExactlyInAnyOrder(
                        tuple("Test", "Description", "nickname"),
                        tuple("Test", "Description", "nickname")
                );

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

    private Project createProject(String creator, Team team) {
        return Project.builder()
                .title("title")
                .repositoryId("repositoryId")
                .creator(creator)
                .team(team)
                .build();
    }

    private Milestone createMilestone(Project project, LocalDateTime startTime, LocalDateTime dueDate) {
        return Milestone.builder()
                .title("Test")
                .description("Description")
                .creator("nickname")
                .startDate(startTime)
                .dueDate(dueDate)
                .project(project)
                .build();
    }
}
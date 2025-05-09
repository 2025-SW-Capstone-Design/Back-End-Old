package soon.capstone.domain.milestone.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class MilestoneCustomRepositoryImplTest extends IntegrationTestSupport {

    @Autowired
    private MilestoneCustomRepositoryImpl milestoneCustomRepositoryImpl;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @AfterEach
    void tearDown() {
        milestoneRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("마감일이 하루 남은 마일스톤을 조회한다.")
    @Test
    void getMilestoneWithIssuesDueTomorrow() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectRepository.save(project);

        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        LocalDateTime startDate = LocalDateTime.now();
        Milestone milestone1 = createMilestone(project, dueDate, startDate);
        Milestone milestone2 = createMilestone(project, dueDate.plusDays(3), startDate);
        milestoneRepository.saveAll(List.of(milestone1, milestone2));

        // when
        List<MilestoneResponse> responses = milestoneCustomRepositoryImpl.getMilestoneWithIssuesDueTomorrow(team.getId());

        // then
        assertThat(responses).hasSize(1)
            .extracting("milestoneId", "startDate", "dueDate")
            .containsExactlyInAnyOrder(
                tuple(milestone1.getId(), startDate, dueDate)
            );
    }

    private Team createTeam() {
        return Team.builder()
            .name("name")
            .description("description")
            .organizationName("organizationName")
            .build();
    }

    private Project createProject(Team team) {
        return Project.builder()
            .creator("creator")
            .title("title")
            .team(team)
            .repositoryId("repositoryId")
            .build();
    }

    private Milestone createMilestone(Project project, LocalDateTime dueDate, LocalDateTime startDate) {
        return Milestone.builder()
            .title("title")
            .description("description")
            .creator("creator")
            .dueDate(dueDate)
            .startDate(startDate)
            .githubMilestoneId(1)
            .project(project)
            .build();
    }

}
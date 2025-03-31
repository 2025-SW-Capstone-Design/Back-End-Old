package soon.capstone.domain.issue.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.issue.repository.issuelabel.IssueLabelRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectJpaRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.global.exception.issue.label.AlreadyIssueLabelException;
import soon.capstone.infrastructure.github.service.GithubIssueLabelService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static soon.capstone.global.exception.dto.ErrorDetail.ISSUE_LABEL_ALREADY_EXISTS;

class IssueLabelServiceTest extends IntegrationTestSupport {

    @Autowired
    private IssueLabelService issueLabelService;

    @Autowired
    private IssueLabelRepository issueLabelRepository;

    @Autowired
    private ProjectJpaRepository projectJpaRepository; // TODO: ProjectRepository로 변경

    @Autowired
    private TeamRepository teamRepository;

    @MockitoBean
    private GithubIssueLabelService githubIssueLabelService;

    @AfterEach
    void tearDown() {
        issueLabelRepository.deleteAllInBatch();
        projectJpaRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("새로운 이슈 라벨을 생성한다.")
    @Test
    void createIssueLabel() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        // when
        Long issueLabelId = issueLabelService.createIssueLabel(
            "title", "description", "color", project, 1L, team
        );

        // then
        IssueLabel savedIssueLabel = issueLabelRepository.findById(issueLabelId);
        assertThat(savedIssueLabel)
            .extracting("id", "title", "description", "color")
            .containsExactly(issueLabelId, "title", "description", "color");

        verify(githubIssueLabelService).createGithubIssueLabel(any());
    }

    @DisplayName("이미 존재하는 이슈 라벨 생성 시 예외가 발생한다.")
    @Test
    void createIssueLabelWithDuplicateTitle() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        IssueLabel issueLabel = IssueLabel.createIssueLabel(
            "color", "title", "description", team, project
        );
        issueLabelRepository.save(issueLabel);

        // expected
        assertThatThrownBy(() ->
            issueLabelService.createIssueLabel("title", "description", "color", project, 1L, team)
        )
            .isInstanceOf(AlreadyIssueLabelException.class)
            .hasMessage(ISSUE_LABEL_ALREADY_EXISTS.getMessage());
    }

    @DisplayName("이슈 라벨을 수정한다.")
    @Test
    void updateIssueLabel() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        IssueLabel issueLabel = IssueLabel.createIssueLabel(
            "color", "oldTitle", "description", team, project
        );
        issueLabelRepository.save(issueLabel);

        // when
        issueLabelService.updateIssueLabel(
            issueLabel.getId(), "oldTitle", "newTitle", "description", "color", "organizationName", "repositoryName", project, 1L
        );

        // then
        IssueLabel updatedIssueLabel = issueLabelRepository.findById(issueLabel.getId());
        assertThat(updatedIssueLabel)
            .extracting("title", "description", "color")
            .containsExactly("newTitle", "description", "color");

        verify(githubIssueLabelService).updateGithubIssueLabel(any());
    }

    @DisplayName("이미 존재하는 라벨명으로 수정 할 경우 예외가 발생한다.")
    @Test
    void updateIssueLabelWithDuplicateTitle() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        IssueLabel issueLabel = IssueLabel.createIssueLabel(
            "color", "oldTitle", "description", team, project
        );
        issueLabelRepository.save(issueLabel);

        // expected
        assertThatThrownBy(() ->
            issueLabelService.updateIssueLabel(
                issueLabel.getId(), "oldTitle", "oldTitle", "description", "color", "organizationName", "repositoryName", project, 1L
            )
        )
            .isInstanceOf(AlreadyIssueLabelException.class)
            .hasMessage(ISSUE_LABEL_ALREADY_EXISTS.getMessage());
    }

    private Team createTeam() {
        return Team.builder()
            .organizationName("organizationName")
            .name("teamName")
            .description("teamDescription")
            .build();
    }

    private Project createProject(Team team) {
        return Project.builder()
            .creator("creator")
            .title("title")
            .team(team)
            .build();
    }

}
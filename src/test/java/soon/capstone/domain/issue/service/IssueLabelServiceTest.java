package soon.capstone.domain.issue.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.issue.repository.issuelabel.IssueLabelRepository;
import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.global.exception.issue.label.AlreadyIssueLabelException;
import soon.capstone.global.exception.issue.label.IssueLabelNotFoundException;
import soon.capstone.infrastructure.github.service.issue.GithubIssueLabelService;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static soon.capstone.global.exception.dto.ErrorDetail.ISSUE_LABEL_ALREADY_EXISTS;
import static soon.capstone.global.exception.dto.ErrorDetail.ISSUE_LABEL_NOT_FOUND;

class IssueLabelServiceTest extends IntegrationTestSupport {

    @Autowired
    private IssueLabelService issueLabelService;

    @Autowired
    private IssueLabelRepository issueLabelRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private GithubIssueLabelService githubIssueLabelService;

    @AfterEach
    void tearDown() {
        Cache cache = cacheManager.getCache("issueLabels");
        if (cache != null) {
            cache.clear();
        }

        issueLabelRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("새로운 이슈 라벨을 생성한다.")
    @Test
    void createIssueLabel() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectRepository.save(project);

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
        projectRepository.save(project);

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
        projectRepository.save(project);

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
        projectRepository.save(project);

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

    @DisplayName("이슈 라벨을 삭제한다.")
    @Test
    void deleteIssueLabel() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectRepository.save(project);

        IssueLabel issueLabel = IssueLabel.createIssueLabel(
            "color", "title", "description", team, project
        );
        issueLabelRepository.save(issueLabel);

        Long issueLabelId = issueLabel.getId();
        String organizationName = team.getOrganizationName();
        String repositoryName = project.getTitle();

        // when
        issueLabelService.deleteIssueLabel(1L, issueLabelId, organizationName, repositoryName, "title");

        // then
        assertThatThrownBy(() -> issueLabelRepository.findById(issueLabelId))
            .isInstanceOf(IssueLabelNotFoundException.class)
            .hasMessage(ISSUE_LABEL_NOT_FOUND.getMessage());

        verify(githubIssueLabelService).deleteGithubIssueLabel(any());
    }

    @DisplayName("이슈 라벨 목록을 조회한다.")
    @Test
    void getIssueLabels() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectRepository.save(project);

        IssueLabel issueLabel1 = IssueLabel.createIssueLabel("color1", "title1", "description1", team, project);
        IssueLabel issueLabel2 = IssueLabel.createIssueLabel("color2", "title2", "description2", team, project);
        issueLabelRepository.saveAll(List.of(issueLabel1, issueLabel2));

        List<IssueLabelDetailResponse> githubLabels = List.of(
            createIssueLabelDetailResponse(null, "title1", "description1", "color1"),
            createIssueLabelDetailResponse(null, "title2", "description2", "color2")
        );

        given(githubIssueLabelService.getIssueLabels(any()))
            .willReturn(githubLabels);

        // when
        List<IssueLabelDetailResponse> result = issueLabelService.getIssueLabels(1L, team, project);

        // then
        assertThat(result)
            .hasSize(2)
            .extracting("labelId", "name", "description", "color")
            .containsExactlyInAnyOrder(
                tuple(issueLabel1.getId(), "title1", "description1", "color1"),
                tuple(issueLabel2.getId(), "title2", "description2", "color2")
            );

        verify(githubIssueLabelService).getIssueLabels(any());
    }

    @DisplayName("이슈 라벨 조회 시 캐시가 동작한다.")
    @Test
    void getIssueLabelsCaching() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectRepository.save(project);

        IssueLabel issueLabel = IssueLabel.createIssueLabel("color", "title", "description", team, project);
        issueLabelRepository.save(issueLabel);

        List<IssueLabelDetailResponse> githubLabels = List.of(
            createIssueLabelDetailResponse(null, "title", "description", "color")
        );

        given(githubIssueLabelService.getIssueLabels(any()))
            .willReturn(githubLabels);

        // when
        issueLabelService.getIssueLabels(1L, team, project);
        issueLabelService.getIssueLabels(1L, team, project);

        // then
        verify(githubIssueLabelService, times(1))
            .getIssueLabels(any());
    }

    @DisplayName("초기 라벨을 저장한다.")
    @Test
    void initializeIssueLabels() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectRepository.save(project);

        List<IssueLabelDetailResponse> githubLabels = List.of(
            createIssueLabelDetailResponse(null, "title1", "description1", "color1"),
            createIssueLabelDetailResponse(null, "title2", "description2", "color2"),
            createIssueLabelDetailResponse(null, "title3", "description3", "color3")
        );

        given(githubIssueLabelService.getIssueLabels(any()))
            .willReturn(githubLabels);

        // when
        issueLabelService.initializeIssueLabels(1L, project, team);

        // then
        List<IssueLabel> savedLabels = issueLabelRepository.findAllByProject(project);
        assertThat(savedLabels)
            .hasSize(3)
            .extracting("title", "description", "color")
            .containsExactlyInAnyOrder(
                tuple("title1", "description1", "color1"),
                tuple("title2", "description2", "color2"),
                tuple("title3", "description3", "color3")
            );

        verify(githubIssueLabelService).getIssueLabels(any());
    }

    @DisplayName("initializeIssueLabels 호출 시 GitHub에서 반환된 라벨이 없을 경우 저장하지 않는다.")
    @Test
    void initializeIssueLabelsWithNoLabels() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectRepository.save(project);

        given(githubIssueLabelService.getIssueLabels(any()))
            .willReturn(List.of());

        // when
        issueLabelService.initializeIssueLabels(1L, project, team);

        // then
        List<IssueLabel> savedLabels = issueLabelRepository.findAllByProject(project);
        assertThat(savedLabels).isEmpty();

        verify(githubIssueLabelService).getIssueLabels(any());
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
            .repositoryId("repositoryId")
            .build();
    }

    private IssueLabelDetailResponse createIssueLabelDetailResponse(Long id, String title, String description, String color) {
        return IssueLabelDetailResponse.builder()
            .labelId(id)
            .name(title)
            .description(description)
            .color(color)
            .build();
    }

}
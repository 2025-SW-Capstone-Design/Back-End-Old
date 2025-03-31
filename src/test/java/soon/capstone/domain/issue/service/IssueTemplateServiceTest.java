package soon.capstone.domain.issue.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.issue.entity.IssueTemplate;
import soon.capstone.domain.issue.repository.issuetemplate.IssueTemplateRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectJpaRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.global.exception.common.InvalidRequest;
import soon.capstone.global.exception.dto.ErrorDetail;
import soon.capstone.global.exception.issue.template.AlreadyIssueTemplateException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static soon.capstone.domain.issue.entity.IssueType.Feature;
import static soon.capstone.domain.issue.entity.IssueType.Fix;
import static soon.capstone.global.exception.dto.ErrorDetail.ISSUE_TEMPLATE_ALREADY_EXISTS;

class IssueTemplateServiceTest extends IntegrationTestSupport {

    @Autowired
    private IssueTemplateService issueTemplateService;

    @Autowired
    private ProjectJpaRepository projectJpaRepository; // TODO: projectRepository 변경

    @Autowired
    private IssueTemplateRepository issueTemplateRepository;

    @Autowired
    private TeamRepository teamRepository;

    @AfterEach
    void tearDown() {
        issueTemplateRepository.deleteAllInBatch();
        projectJpaRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("이슈 템플릿이 생성된다.")
    @Test
    void createIssueTemplate() {
        // given
        String title = "title";
        String description = "description";
        String content = "content";
        String type = "Feature";

        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        // when
        Long issueTemplateId = issueTemplateService.createIssueTemplate(title, description, content, type, project);

        // then
        IssueTemplate template = issueTemplateRepository.findById(issueTemplateId);
        assertThat(template)
            .extracting("title", "description", "content", "type")
            .containsExactlyInAnyOrder(title, description, content, Feature);
    }

    @DisplayName("템플릿 생성 시 유효하지 않은 타입은 예외가 발생한다.")
    @Test
    void createIssueTemplateWithInvalidIssueType() {
        // given
        String title = "title";
        String description = "description";
        String content = "content";
        String type = "invalidType";

        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        // expected
        assertThatThrownBy(() -> issueTemplateService.createIssueTemplate(title, description, content, type, project))
            .isInstanceOf(InvalidRequest.class)
            .hasMessage(ErrorDetail.INVALID_REQUEST.getMessage());
    }

    @DisplayName("템플릿 생성 시 이미 존재하는 이름이라면 예외가 발생한다.")
    @Test
    void createIssueTemplateWithDuplicatedTitle() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        IssueTemplate template = createIssueTemplate(project);
        issueTemplateRepository.save(template);

        // expected
        assertThatThrownBy(() ->
            issueTemplateService.createIssueTemplate(template.getTitle(), template.getDescription(), template.getContent(), "Feature", project)
        )
            .isInstanceOf(AlreadyIssueTemplateException.class)
            .hasMessage(ISSUE_TEMPLATE_ALREADY_EXISTS.getMessage());
    }

    @DisplayName("이슈 템플릿을 수정한다.")
    @Test
    void updateIssueTemplate() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        IssueTemplate template = createIssueTemplate(project);
        issueTemplateRepository.save(template);

        String newTitle = "newTitle";
        String newDescription = "newDescription";
        String newContent = "newContent";
        String newType = Fix.name();

        // when
        issueTemplateService.updateIssueTemplate(template.getId(), newTitle, newDescription, newContent, newType, project);

        // then
        IssueTemplate updatedTemplate = issueTemplateRepository.findById(template.getId());
        assertThat(updatedTemplate)
            .extracting("title", "description", "content", "type")
            .containsExactlyInAnyOrder(newTitle, newDescription, newContent, Fix);
    }

    @DisplayName("이슈 템플릿 수정 시 존재하는 이름으로 변경하면 예외가 발생한다.")
    @Test
    void updateIssueTemplateWithDuplicatedTitle() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        IssueTemplate template = createIssueTemplate(project);
        issueTemplateRepository.save(template);

        // expected
        assertThatThrownBy(() ->
            issueTemplateService.updateIssueTemplate(template.getId(), template.getTitle(), template.getDescription(), template.getContent(), "Feature", project)
        )
            .isInstanceOf(AlreadyIssueTemplateException.class)
            .hasMessage(ISSUE_TEMPLATE_ALREADY_EXISTS.getMessage());
    }

    @DisplayName("템플릿 수정 시 유효하지 않은 타입으로 변경하면 예외가 발생한다.")
    @Test
    void updateIssueTemplateWithInvalidIssueType() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        IssueTemplate template = createIssueTemplate(project);
        issueTemplateRepository.save(template);

        String invalidType = "invalidType";

        // expected
        assertThatThrownBy(() ->
            issueTemplateService.updateIssueTemplate(template.getId(), "newTitle", "newDescription", "newContent", invalidType, project)
        )
            .isInstanceOf(InvalidRequest.class)
            .hasMessage(ErrorDetail.INVALID_REQUEST.getMessage());
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

    private IssueTemplate createIssueTemplate(Project project) {
        return IssueTemplate.builder()
            .title("title")
            .description("description")
            .content("content")
            .type(Feature)
            .project(project)
            .build();
    }

}
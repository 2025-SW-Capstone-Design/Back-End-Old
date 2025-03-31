package soon.capstone.domain.issue.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.issue.entity.IssueTemplate;
import soon.capstone.domain.issue.entity.IssueType;
import soon.capstone.domain.issue.repository.issuetemplate.IssueTemplateRepository;
import soon.capstone.domain.issue.service.dto.response.IssueTemplateDetailResponse;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectJpaRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.global.exception.common.InvalidRequest;
import soon.capstone.global.exception.dto.ErrorDetail;
import soon.capstone.global.exception.issue.template.AlreadyIssueTemplateException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static soon.capstone.domain.issue.entity.IssueType.*;
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

        IssueTemplate template = createIssueTemplate(project, Feature);
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

        IssueTemplate template = createIssueTemplate(project, Feature);
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

        IssueTemplate template = createIssueTemplate(project, Feature);
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

        IssueTemplate template = createIssueTemplate(project, Feature);
        issueTemplateRepository.save(template);

        String invalidType = "invalidType";

        // expected
        assertThatThrownBy(() ->
            issueTemplateService.updateIssueTemplate(template.getId(), "newTitle", "newDescription", "newContent", invalidType, project)
        )
            .isInstanceOf(InvalidRequest.class)
            .hasMessage(ErrorDetail.INVALID_REQUEST.getMessage());
    }

    @DisplayName("템플릿 ID로 템플릿을 조회한다.")
    @Test
    void getIssueTemplate() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        IssueTemplate template = createIssueTemplate(project, Feature);
        issueTemplateRepository.save(template);

        // when
        IssueTemplateDetailResponse response = issueTemplateService.getIssueTemplate(template.getId());

        // then
        assertThat(response)
            .extracting("title", "description", "content", "type")
            .containsExactlyInAnyOrder(template.getTitle(), template.getDescription(), template.getContent(), Feature.name());
    }

    @DisplayName("타입별로 프로젝트의 이슈 템플릿을 조회한다.")
    @Test
    void getIssueTemplatesByType() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        IssueTemplate template1 = createIssueTemplate(project, Feature);
        IssueTemplate template2 = createIssueTemplate(project, Feature);
        IssueTemplate template3 = createIssueTemplate(project, Fix);
        issueTemplateRepository.saveAll(List.of(template1, template2, template3));

        // when
        List<IssueTemplateDetailResponse> responses = issueTemplateService.getIssueTemplates(Feature.name(), project);

        // then
        assertThat(responses).hasSize(2)
            .extracting("title", "description", "content", "type")
            .containsExactlyInAnyOrder(
                tuple("title", "description", "content", Feature.name()),
                tuple("title", "description", "content", Feature.name())
            );
    }

    @DisplayName("특정 타입의 템플릿이 없는경우 빈 리스트를 반환한다.")
    @Test
    void getIssueTemplatesByTypeWithEmptyResult() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        // when
        List<IssueTemplateDetailResponse> responses = issueTemplateService.getIssueTemplates(Feature.name(), project);

        // then
        assertThat(responses)
            .isEmpty();
    }

    @DisplayName("프로젝트의 모든 이슈 템플릿을 조회한다.")
    @Test
    void getIssueTemplatesByProject() {
        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        IssueTemplate template1 = createIssueTemplate(project, Feature);
        IssueTemplate template2 = createIssueTemplate(project, Refactor);
        IssueTemplate template3 = createIssueTemplate(project, Fix);
        issueTemplateRepository.saveAll(List.of(template1, template2, template3));

        // when
        List<IssueTemplateDetailResponse> responses = issueTemplateService.getIssueTemplates(null, project);

        // then
        assertThat(responses).hasSize(3)
            .extracting("title", "description", "content", "type")
            .containsExactlyInAnyOrder(
                tuple("title", "description", "content", Feature.name()),
                tuple("title", "description", "content", Refactor.name()),
                tuple("title", "description", "content", Fix.name())
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

    private IssueTemplate createIssueTemplate(Project project, IssueType issueType) {
        return IssueTemplate.builder()
            .title("title")
            .description("description")
            .content("content")
            .type(issueType)
            .project(project)
            .build();
    }

}
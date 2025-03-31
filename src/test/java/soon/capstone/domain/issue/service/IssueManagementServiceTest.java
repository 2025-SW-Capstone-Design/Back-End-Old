package soon.capstone.domain.issue.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.issue.entity.IssueTemplate;
import soon.capstone.domain.issue.repository.issuelabel.IssueLabelRepository;
import soon.capstone.domain.issue.repository.issuetemplate.IssueTemplateRepository;
import soon.capstone.domain.issue.service.dto.request.IssueLabelCreateServiceRequest;
import soon.capstone.domain.issue.service.dto.request.IssueLabelUpdateServiceRequest;
import soon.capstone.domain.issue.service.dto.request.IssueTemplateCreateServiceRequest;
import soon.capstone.domain.issue.service.dto.request.IssueTemplateUpdateServiceRequest;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectJpaRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.team.TeamNotAuthorizedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static soon.capstone.domain.issue.entity.IssueType.Feature;
import static soon.capstone.domain.issue.entity.IssueType.Fix;
import static soon.capstone.global.exception.dto.ErrorDetail.TEAM_NOT_AUTHORIZED;

class IssueManagementServiceTest extends IntegrationTestSupport {

    @Autowired
    private IssueManagementService issueManagementService;

    @Autowired
    private IssueLabelRepository issueLabelRepository;

    @Autowired
    private IssueTemplateRepository issueTemplateRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProjectJpaRepository projectJpaRepository; // TODO: ProjectRepository로 변경

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @MockitoBean
    private IssueLabelService issueLabelService;

    @MockitoBean
    private IssueTemplateService issueTemplateService;

    @AfterEach
    void tearDown() {
        issueLabelRepository.deleteAllInBatch();
        issueTemplateRepository.deleteAllInBatch();
        projectJpaRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("이슈 라벨을 생성한다.")
    @Test
    void createIssueLabel() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        var request = createIssueLabelCreateServiceRequest(team, project);

        given(issueLabelService.createIssueLabel(
            anyString(),
            anyString(),
            anyString(),
            any(Project.class),
            anyLong(),
            any(Team.class)
        )).willReturn(1L);

        // when
        Long issueLabelId = issueManagementService.createIssueLabel(request, member.getId());

        // then
        assertThat(issueLabelId)
            .isEqualTo(1L);
    }

    @DisplayName("팀에 속하지 않은 멤버가 이슈 라벨을 생성 할 경우 예외가 발생한다")
    @Test
    void createIssueLabelWithNotTeamMember() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        // expected
        assertThatThrownBy(() -> {
            issueManagementService.createIssueLabel(
                createIssueLabelCreateServiceRequest(team, project),
                member.getId()
            );
        }).isInstanceOf(TeamNotAuthorizedException.class)
            .hasMessage(TEAM_NOT_AUTHORIZED.getMessage());
    }

    @DisplayName("이슈 템플릿을 생성한다.")
    @Test
    void createIssueTemplate() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        var request = createIssueTemplateCreateServiceRequest(team, project);

        given(issueTemplateService.createIssueTemplate(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            any(Project.class)
        )).willReturn(1L);

        // when
        Long issueTemplateId = issueManagementService.createIssueTemplate(request, member.getId());

        // then
        assertThat(issueTemplateId)
            .isEqualTo(1L);
    }

    @DisplayName("팀에 속하지 않은 멤버가 이슈 템플릿을 생성 할 경우 예외가 발생한다")
    @Test
    void createIssueTemplateWithNotTeamMember() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        // expected
        assertThatThrownBy(() -> {
            issueManagementService.createIssueTemplate(
                createIssueTemplateCreateServiceRequest(team, project),
                member.getId()
            );
        }).isInstanceOf(TeamNotAuthorizedException.class)
            .hasMessage(TEAM_NOT_AUTHORIZED.getMessage());
    }

    @DisplayName("이슈 라벨을 수정한다")
    @Test
    void updateIssueLabel() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        IssueLabel issueLabel = IssueLabel.createIssueLabel(
            "color", "title", "description", team, project
        );
        issueLabelRepository.save(issueLabel);

        var request = createIssueLabelUpdateServiceRequest(
            issueLabel.getId(), project.getId(), team.getId(), "newTitle", issueLabel.getTitle()
        );

        mockIssueLabelUpdate();

        // when
        issueManagementService.updateIssueLabel(request, member.getId());

        // then
        IssueLabel updatedLabel = issueLabelRepository.findById(issueLabel.getId());
        assertThat(updatedLabel.getTitle()).isEqualTo("newTitle");
    }

    @DisplayName("이슈 템플릿을 수정한다")
    @Test
    void updateIssueTemplate() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        IssueTemplate template = IssueTemplate.builder()
            .title("title")
            .description("description")
            .content("content")
            .project(project)
            .type(Feature)
            .build();
        issueTemplateRepository.save(template);

        var request = createIssueTemplateUpdateServiceRequest(team, project);

        mockIssueTemplateUpdate();

        // when
        issueManagementService.updateIssueTemplate(request, member.getId());

        // then
        verify(issueTemplateService)
            .updateIssueTemplate(anyLong(), anyString(), anyString(), anyString(), anyString(), any(Project.class));
    }

    @DisplayName("팀에 속하지 않은 멤버가 템플릿을 수정할 경우 예외가 발생한다.")
    @Test
    void updateIssueTemplateWithNotTeamMember() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        var request = createIssueTemplateUpdateServiceRequest(team, project);

        // expected
        assertThatThrownBy(() -> {
            issueManagementService.updateIssueTemplate(request, member.getId());
        }).isInstanceOf(TeamNotAuthorizedException.class)
            .hasMessage(TEAM_NOT_AUTHORIZED.getMessage());
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

    private IssueLabelCreateServiceRequest createIssueLabelCreateServiceRequest(Team team, Project project) {
        return IssueLabelCreateServiceRequest.builder()
            .color("color")
            .teamId(team.getId())
            .title("title")
            .description("description")
            .projectId(project.getId())
            .build();
    }

    private IssueTemplateCreateServiceRequest createIssueTemplateCreateServiceRequest(Team team, Project project) {
        return IssueTemplateCreateServiceRequest.builder()
            .content("content")
            .teamId(team.getId())
            .title("title")
            .description("description")
            .projectId(project.getId())
            .type("type")
            .build();
    }

    private IssueLabelUpdateServiceRequest createIssueLabelUpdateServiceRequest(Long labelId, Long projectId, Long teamId, String newTitle, String oldTitle) {
        return IssueLabelUpdateServiceRequest.builder()
            .color("color")
            .description("description")
            .labelId(labelId)
            .newTitle(newTitle)
            .oldTitle(oldTitle)
            .organizationName("organizationName")
            .projectId(projectId)
            .repositoryName("repositoryName")
            .teamId(teamId)
            .build();
    }

    private void mockIssueLabelUpdate() {
        doAnswer(invocation -> {
            Long labelId = invocation.getArgument(0);
            String newTitle = invocation.getArgument(2);

            IssueLabel label = issueLabelRepository.findById(labelId);
            label.update(newTitle, "description", "color");
            issueLabelRepository.save(label);
            return null;
        }).when(issueLabelService).updateIssueLabel(
            anyLong(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), any(Project.class), anyLong()
        );
    }

    private IssueTemplateUpdateServiceRequest createIssueTemplateUpdateServiceRequest(Team team, Project project) {
        return IssueTemplateUpdateServiceRequest.builder()
            .issueTemplateId(1L)
            .content("updatedContent")
            .teamId(team.getId())
            .title("updatedTitle")
            .description("updatedDescription")
            .projectId(project.getId())
            .type(Fix.name())
            .build();
    }

    private void mockIssueTemplateUpdate() {
        doAnswer(invocation -> {
            Long templateId = invocation.getArgument(0);
            String title = invocation.getArgument(1);
            String description = invocation.getArgument(2);
            String content = invocation.getArgument(3);

            IssueTemplate template = issueTemplateRepository.findById(templateId);
            template.update(title, description, content, Fix);
            issueTemplateRepository.save(template);

            return null;
        }).when(issueTemplateService).updateIssueTemplate(
            anyLong(), anyString(), anyString(), anyString(), anyString(), any(Project.class)
        );
    }

}
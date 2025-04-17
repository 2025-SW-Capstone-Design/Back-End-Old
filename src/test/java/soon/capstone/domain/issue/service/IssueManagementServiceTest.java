package soon.capstone.domain.issue.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.issue.entity.Issue;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.issue.entity.IssueStatus;
import soon.capstone.domain.issue.entity.IssueTemplate;
import soon.capstone.domain.issue.repository.issue.IssueRepository;
import soon.capstone.domain.issue.repository.issuelabel.IssueLabelRepository;
import soon.capstone.domain.issue.repository.issuetemplate.IssueTemplateRepository;
import soon.capstone.domain.issue.service.dto.request.*;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;
import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;
import soon.capstone.domain.issue.service.dto.response.IssueTemplateDetailResponse;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.team.TeamNotAuthorizedException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static soon.capstone.domain.issue.entity.IssueType.*;
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
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private IssueRepository issueRepository;

    @MockitoBean
    private IssueLabelService issueLabelService;

    @MockitoBean
    private IssueTemplateService issueTemplateService;

    @MockitoBean
    private IssueService issueService;

    @AfterEach
    void tearDown() {
        issueLabelRepository.deleteAllInBatch();
        issueTemplateRepository.deleteAllInBatch();
        issueRepository.deleteAllInBatch();
        milestoneRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
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
        projectRepository.save(project);

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
        projectRepository.save(project);

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
        projectRepository.save(project);

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
        projectRepository.save(project);

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
        projectRepository.save(project);

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
        projectRepository.save(project);

        IssueTemplate template = IssueTemplate.builder()
            .title("title")
            .description("description")
            .content("content")
            .project(project)
            .type(Feature)
            .build();
        issueTemplateRepository.save(template);

        var request = createIssueTemplateUpdateServiceRequest(team, project, template.getId());

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
        projectRepository.save(project);

        var request = createIssueTemplateUpdateServiceRequest(team, project, 1L);

        // expected
        assertThatThrownBy(() -> {
            issueManagementService.updateIssueTemplate(request, member.getId());
        }).isInstanceOf(TeamNotAuthorizedException.class)
            .hasMessage(TEAM_NOT_AUTHORIZED.getMessage());
    }

    @DisplayName("이슈 템플릿을 ID로 조회한다")
    @Test
    void getIssueTemplate() {
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        var expectedResponse = createIssueTemplateDetailResponse(1L, "title", Feature.name());
        given(issueTemplateService.getIssueTemplate(anyLong()))
            .willReturn(expectedResponse);

        // when
        var response = issueManagementService.getIssueTemplate(
            team.getId(), 1L, member.getId());

        // then
        assertThat(response)
            .extracting("id", "title", "description", "content", "type")
            .containsExactly(1L, "title", "description", "content", "Feature");
    }

    @DisplayName("팀에 속하지 않은 멤버가 이슈 템플릿을 조회할 경우 예외가 발생한다")
    @Test
    void getIssueTemplateWithNotTeamMember() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        // expected
        assertThatThrownBy(() -> {
            issueManagementService.getIssueTemplate(team.getId(), 1L, member.getId());
        }).isInstanceOf(TeamNotAuthorizedException.class)
            .hasMessage(TEAM_NOT_AUTHORIZED.getMessage());
    }

    @DisplayName("프로젝트의 이슈 템플릿 목록을 조회한다")
    @Test
    void getIssueTemplatesByProject() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        var template1 = createIssueTemplateDetailResponse(1L, "title1", Feature.name());
        var template2 = createIssueTemplateDetailResponse(2L, "title2", Refactor.name());
        given(issueTemplateService.getIssueTemplates(any(), any(Project.class)))
            .willReturn(List.of(template1, template2));

        // when
        List<IssueTemplateDetailResponse> response = issueManagementService.getIssueTemplates(
            team.getId(), member.getId(), project.getId(), null
        );

        // then
        assertThat(response)
            .hasSize(2)
            .extracting("id", "title", "type")
            .containsExactlyInAnyOrder(
                tuple(1L, "title1", "Feature"),
                tuple(2L, "title2", "Refactor")
            );
    }

    @DisplayName("타입으로 필터링된 이슈 템플릿 목록을 조회한다")
    @Test
    void getIssueTemplatesByType() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        var template1 = createIssueTemplateDetailResponse(1L, "title1", Feature.name());
        given(issueTemplateService.getIssueTemplates(anyString(), any(Project.class)))
            .willReturn(List.of(template1));

        // when
        List<IssueTemplateDetailResponse> response = issueManagementService.getIssueTemplates(
            team.getId(), member.getId(), project.getId(), Feature.name()
        );

        // then
        assertThat(response)
            .hasSize(1)
            .extracting("id", "title", "type")
            .contains(
                tuple(1L, "title1", "Feature")
            );
        verify(issueTemplateService).getIssueTemplates(anyString(), any(Project.class));
    }

    @DisplayName("이슈 템플릿을 삭제한다")
    @Test
    void deleteIssueTemplate() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        IssueTemplate template = IssueTemplate.builder()
            .title("title")
            .description("description")
            .content("content")
            .project(project)
            .type(Feature)
            .build();
        issueTemplateRepository.save(template);

        // when
        issueManagementService.deleteIssueTemplate(team.getId(), template.getId(), member.getId());

        // then
        verify(issueTemplateService).deleteIssueTemplate(template.getId());
    }

    @DisplayName("팀에 속하지 않은 멤버가 이슈 템플릿을 삭제할 경우 예외가 발생한다")
    @Test
    void deleteIssueTemplateWithNotTeamMember() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectRepository.save(project);

        IssueTemplate template = IssueTemplate.builder()
            .title("title")
            .description("description")
            .content("content")
            .project(project)
            .type(Feature)
            .build();
        issueTemplateRepository.save(template);

        // expected
        assertThatThrownBy(() -> {
            issueManagementService.deleteIssueTemplate(team.getId(), template.getId(), member.getId());
        }).isInstanceOf(TeamNotAuthorizedException.class)
            .hasMessage(TEAM_NOT_AUTHORIZED.getMessage());
    }

    @DisplayName("이슈라벨을 삭제한다.")
    @Test
    void deleteIssueLabel() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        IssueLabel issueLabel = IssueLabel.createIssueLabel("color", "title", "description", team, project);
        issueLabelRepository.save(issueLabel);

        IssueLabelDeleteServiceRequest request = IssueLabelDeleteServiceRequest.builder()
            .labelId(issueLabel.getId())
            .teamId(team.getId())
            .title("title")
            .repositoryName("repository")
            .organizationName("organization")
            .build();

        // when
        issueManagementService.deleteIssueLabel(request, member.getId());

        // then
        verify(issueLabelService).deleteIssueLabel(
            member.getId(),
            issueLabel.getId(),
            request.organizationName(),
            request.repositoryName(),
            request.title()
        );
    }

    @DisplayName("팀에 속하지 않은 멤버가 이슈 라벨을 삭제할 경우 예외가 발생한다")
    @Test
    void deleteIssueLabelWithNotTeamMember() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectRepository.save(project);

        IssueLabel issueLabel = IssueLabel.createIssueLabel("color", "title", "description", team, project);
        issueLabelRepository.save(issueLabel);

        IssueLabelDeleteServiceRequest request = IssueLabelDeleteServiceRequest.builder()
            .labelId(issueLabel.getId())
            .teamId(team.getId())
            .title("title")
            .repositoryName("repository")
            .organizationName("organization")
            .build();

        // expected
        assertThatThrownBy(() -> {
            issueManagementService.deleteIssueLabel(request, member.getId());
        }).isInstanceOf(TeamNotAuthorizedException.class)
            .hasMessage(TEAM_NOT_AUTHORIZED.getMessage());
    }

    @DisplayName("프로젝트의 이슈 라벨 목록을 조회한다")
    @Test
    void getIssueLabelsByProject() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        var label1 = IssueLabelDetailResponse.builder()
            .labelId(1L)
            .name("bug")
            .color("#ff0000")
            .description("버그 라벨")
            .build();

        var label2 = IssueLabelDetailResponse.builder()
            .labelId(2L)
            .name("enhancement")
            .color("#0000ff")
            .description("기능 개선 라벨")
            .build();

        given(issueLabelService.getIssueLabels(anyLong(), any(Team.class), any(Project.class)))
            .willReturn(List.of(label1, label2));

        var request = IssueLabelDetailServiceRequest.builder()
            .teamId(team.getId())
            .projectId(project.getId())
            .build();

        // when
        List<IssueLabelDetailResponse> response = issueManagementService.getIssueLabels(request, member.getId());

        // then
        assertThat(response)
            .hasSize(2)
            .extracting("labelId", "name", "color")
            .containsExactlyInAnyOrder(
                tuple(label1.getLabelId(), "bug", "#ff0000"),
                tuple(label2.getLabelId(), "enhancement", "#0000ff")
            );
    }

    @DisplayName("팀에 속하지 않은 멤버가 이슈 라벨을 조회할 경우 예외가 발생한다")
    @Test
    void getIssueLabelsWithNotTeamMember() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectRepository.save(project);

        var request = IssueLabelDetailServiceRequest.builder()
            .teamId(team.getId())
            .projectId(project.getId())
            .build();

        // expected
        assertThatThrownBy(() -> {
            issueManagementService.getIssueLabels(request, member.getId());
        }).isInstanceOf(TeamNotAuthorizedException.class)
            .hasMessage(TEAM_NOT_AUTHORIZED.getMessage());
    }

    @DisplayName("이슈를 생성한다")
    @Test
    void createIssue() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        Milestone milestone = createMilestone(project);
        milestoneRepository.save(milestone);

        var request = IssueCreateServiceRequest.builder()
            .teamId(team.getId())
            .memberId(member.getId())
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .title("title")
            .content("content")
            .assignees("assignee")
            .labels(List.of("label1"))
            .milestoneId(milestone.getId())
            .projectId(project.getId())
            .build();

        given(issueService.create(
            anyLong(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyList(),
            any(Milestone.class),
            any(Project.class),
            any(TeamMember.class)
        )).willReturn(1L);

        // when
        Long issueId = issueManagementService.createIssue(request);

        // then
        assertThat(issueId)
            .isEqualTo(1L);
    }

    @DisplayName("이슈를 수정한다")
    @Test
    void updateIssue() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Member member = createMember();
        memberRepository.save(member);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        Milestone milestone = createMilestone(project);
        milestoneRepository.save(milestone);

        var request = IssueUpdateServiceRequest.builder()
            .teamId(team.getId())
            .memberId(member.getId())
            .milestoneId(milestone.getId())
            .issueId(1L)
            .organizationName("org")
            .repositoryName("repo")
            .title("Updated Title")
            .content("Updated Content")
            .labels(List.of("label1", "label2"))
            .assignees("assignee")
            .state(IssueStatus.CLOSED.name())
            .build();

        // when
        issueManagementService.updateIssue(request);

        // then
        verify(issueService).update(
            anyLong(),
            anyLong(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyList(),
            anyString(),
            anyString(),
            any(TeamMember.class),
            any(Milestone.class)
        );
    }

    @DisplayName("이슈의 상태를 CLOSED로 변경 한다.")
    @Test
    void closedIssue() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        Member member = createMember();
        memberRepository.save(member);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        Milestone milestone = createMilestone(project);
        milestoneRepository.save(milestone);

        Issue issue = createIssue(project, teamMember, milestone);
        issueRepository.save(issue);

        var request = IssueClosedServiceRequest.builder()
            .memberId(member.getId())
            .teamId(team.getId())
            .issueId(issue.getId())
            .organizationName("organizationName")
            .repositoryName("repositoryName")
            .build();

        // when
        issueManagementService.closedIssue(request);

        // then
        verify(issueService).closedIssue(
            anyLong(),
            anyLong(),
            anyString(),
            anyString()
        );
    }

    @DisplayName("이슈의 상세 정보를 조회한다")
    @Test
    void getIssueDetail() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        Milestone milestone = createMilestone(project);
        milestoneRepository.save(milestone);

        Issue issue = createIssue(project, teamMember, milestone);
        issueRepository.save(issue);

        IssueDetailServiceRequest request = IssueDetailServiceRequest.builder()
            .memberId(member.getId())
            .teamId(team.getId())
            .projectId(project.getId())
            .issueId(issue.getId())
            .build();

        IssueDetailResponse mockResponse = IssueDetailResponse.builder()
            .issueId(issue.getId())
            .title("title")
            .content("content")
            .creator("creator")
            .status("open")
            .labels(List.of())
            .build();

        given(issueService.getIssueDetail(
            member.getId(),
            issue.getId(),
            team.getOrganizationName(),
            project.getTitle()
        )).willReturn(mockResponse);

        // when
        IssueDetailResponse response = issueManagementService.getIssueDetail(request);

        // then
        assertThat(response)
            .extracting("issueId", "title", "content", "creator", "status")
            .containsExactly(issue.getId(), "title", "content", "creator", "open");
    }

    @DisplayName("scope가 project인 경우 프로젝트의 모든 이슈 목록을 조회한다. ")
    @Test
    void getIssuesWithProject() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        var request = IssueDetailListServiceRequest.builder()
            .memberId(member.getId())
            .teamId(team.getId())
            .projectId(project.getId())
            .scope("project")
            .build();

        given(issueService.getIssuesWithRepository(
            anyLong(),
            anyString(),
            anyString()
        )).willReturn(List.of(
            createIssueDetailResponse(1L, "title1"),
            createIssueDetailResponse(2L, "title2")
        ));

        // when
        List<IssueDetailResponse> responses = issueManagementService.getIssues(request);

        // then
        assertThat(responses).hasSize(2)
            .extracting("issueId", "title")
            .containsExactlyInAnyOrder(
                tuple(1L, "title1"),
                tuple(2L, "title2")
            );

        verify(issueService).getIssuesWithRepository(
            anyLong(),
            anyString(),
            anyString()
        );
    }

    @DisplayName("scope가 team인 경우 팀의 모든 이슈 목록을 조회한다. ")
    @Test
    void getIssuesWithTeam() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        var request = IssueDetailListServiceRequest.builder()
            .memberId(member.getId())
            .teamId(team.getId())
            .projectId(project.getId())
            .scope("team")
            .build();

        given(issueService.getIssuesWithOrganization(
            anyLong(),
            anyString(),
            anyString()
        )).willReturn(List.of(
            createIssueDetailResponse(1L, "title1"),
            createIssueDetailResponse(2L, "title2")
        ));

        // when
        List<IssueDetailResponse> responses = issueManagementService.getIssues(request);

        // then
        assertThat(responses).hasSize(2)
            .extracting("issueId", "title")
            .containsExactlyInAnyOrder(
                tuple(1L, "title1"),
                tuple(2L, "title2")
            );

        verify(issueService).getIssuesWithOrganization(
            anyLong(),
            anyString(),
            anyString()
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

    private Milestone createMilestone(Project project) {
        return Milestone.builder()
            .title("title")
            .description("description")
            .creator("creator")
            .dueDate(LocalDateTime.of(2025, 4, 12, 0, 0))
            .startDate(LocalDateTime.of(2025, 4, 11, 0, 0))
            .githubMilestoneId(1)
            .project(project)
            .build();
    }

    private Issue createIssue(Project project, TeamMember teamMember, Milestone milestone) {
        return Issue.builder()
            .title("title")
            .githubIssueNumber(1L)
            .status(IssueStatus.OPEN)
            .content("content")
            .project(project)
            .teamMember(teamMember)
            .milestone(milestone)
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

    private IssueTemplateUpdateServiceRequest createIssueTemplateUpdateServiceRequest(Team team, Project project, long issueTemplateId) {
        return IssueTemplateUpdateServiceRequest.builder()
            .issueTemplateId(issueTemplateId)
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

    private IssueTemplateDetailResponse createIssueTemplateDetailResponse(long id, String title, String feature) {
        return IssueTemplateDetailResponse.builder()
            .id(id)
            .title(title)
            .description("description")
            .content("content")
            .type(feature)
            .build();
    }

    private IssueDetailResponse createIssueDetailResponse(long id, String title) {
        return IssueDetailResponse.builder()
            .issueId(id)
            .title(title)
            .content("content")
            .creator("creator")
            .status("status")
            .labels(List.of())
            .build();
    }

}
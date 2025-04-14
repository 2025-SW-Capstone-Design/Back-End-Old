package soon.capstone.domain.milestone.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.issue.entity.Issue;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.issue.entity.IssueLabelRelation;
import soon.capstone.domain.issue.entity.IssueStatus;
import soon.capstone.domain.issue.repository.issue.IssueRepository;
import soon.capstone.domain.issue.repository.issueLabelRelation.IssueLabelRelationRepository;
import soon.capstone.domain.issue.repository.issuelabel.IssueLabelRepository;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;
import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.milestone.service.dto.request.MilestoneUpdateServiceRequest;
import soon.capstone.domain.milestone.service.dto.response.MilestoneDetailResponse;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.milestone.service.dto.request.MilestoneCreateServiceRequest;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.entity.common.Position;
import soon.capstone.domain.teammember.entity.common.Role;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
class MilestoneServiceTest extends IntegrationTestSupport {

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OAuthTokenRepository oAuthTokenRepository;

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

    @Autowired
    private IssueLabelRepository issueLabelRepository;

    @Autowired
    private IssueLabelRelationRepository issueLabelRelationRepository;

    @MockitoBean
    private MilestoneCreationService milestoneCreationService;

    @MockitoBean
    private MilestoneReadService milestoneReadService;

    @MockitoBean
    private MilestoneUpdateService milestoneUpdateService;

    @AfterEach
    void tearDown() {
        issueLabelRelationRepository.deleteAllInBatch();
        issueLabelRepository.deleteAllInBatch();
        issueRepository.deleteAllInBatch();
        milestoneRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("마일스톤을 생성한다.")
    @Test
    void createMilestone() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = createTeamMember(member, team);
        teamMemberRepository.save(teamMember);

        OAuthToken oauthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oauthToken);

        Project project = createProject(member.getNickname(), team);
        projectRepository.save(project);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        Long memberId = member.getId();
        var request = MilestoneCreateServiceRequest.builder()
                .title("title")
                .description("description")
                .startDate(startDate)
                .dueDate(dueDate)
                .build();

        // When
        milestoneService.createMilestone(memberId, team.getId(), project.getId(), request);

        // Then
        verify(milestoneCreationService).createMilestone(any());
    }

    @DisplayName("프로젝트에 속한 마일스톤들을 반환한다.")
    @Test
    void getMilestonesByProject() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = createTeamMember(member, team);
        teamMemberRepository.save(teamMember);

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

        given(milestoneReadService.getMilestonesByProject(any()))
                .willReturn(List.of(
                        createMilestoneResponse(1L, startDate, dueDate),
                        createMilestoneResponse(2L, startDate, dueDate)
                ));

        // When
        List<MilestoneResponse> milestones = milestoneService.getMilestonesByProject(member.getId(), team.getId(), project.getId());

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

        TeamMember teamMember = createTeamMember(member, team);
        teamMemberRepository.save(teamMember);

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

        given(milestoneReadService.getMilestonesByTeam(any()))
                .willReturn(List.of(
                        createMilestoneResponse(1L, startDate, dueDate),
                        createMilestoneResponse(2L, startDate, dueDate)
                ));

        // When
        List<MilestoneResponse> milestones = milestoneService.getMilestonesByTeam(member.getId(), team.getId());

        // Then
        assertThat(milestones).hasSize(2);
        assertThat(milestones)
                .extracting("title", "description", "creator")
                .containsExactlyInAnyOrder(
                        tuple("Test", "Description", "nickname"),
                        tuple("Test", "Description", "nickname")
                );
    }

    @DisplayName("마일스톤 상세 정보를 조회할 수 있다.")
    @Test
    void getMilestoneDetail() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = createTeamMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(member.getNickname(), team);
        projectRepository.save(project);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        Milestone milestone = createMilestone(project, startDate, dueDate);
        milestoneRepository.save(milestone);

        Issue issue = createIssue("title", "content", teamMember, milestone, project);
        issueRepository.save(issue);

        IssueLabel label = createLabel("label", "color", "description", team, project);
        issueLabelRepository.save(label);

        IssueLabelRelation relation = createRelation(issue, label);
        issueLabelRelationRepository.save(relation);

        given(milestoneReadService.getMilestoneDetail(any()))
                .willReturn(
                        MilestoneDetailResponse.builder()
                                .milestoneId(milestone.getId())
                                .title("Test")
                                .description("Description")
                                .creator("nickname")
                                .startDate(startDate)
                                .dueDate(dueDate)
                                .isCompleted(false)
                                .issues(List.of(
                                        createIssues(issue, label)
                                ))
                                .build()
                );

        // When
        MilestoneDetailResponse milestoneDetail = milestoneService.getMilestoneDetail(member.getId(), team.getId(), milestone.getId());

        // Then
        assertThat(milestoneDetail)
                .isNotNull()
                .satisfies(detail -> {
                    assertThat(detail.milestoneId()).isEqualTo(milestone.getId());
                    assertThat(detail.title()).isEqualTo("Test");
                    assertThat(detail.description()).isEqualTo("Description");
                    assertThat(detail.creator()).isEqualTo("nickname");
                    assertThat(detail.startDate()).isEqualTo(startDate);
                    assertThat(detail.dueDate()).isEqualTo(dueDate);
                    assertThat(detail.isCompleted()).isFalse();
                });

        assertThat(milestoneDetail.issues())
                .hasSize(1)
                .first()
                .satisfies(i -> {
                    assertThat(i.issueId()).isEqualTo(issue.getId());
                    assertThat(i.title()).isEqualTo(issue.getTitle());
                    assertThat(i.content()).isEqualTo(issue.getContent());
                    assertThat(i.creator()).isEqualTo(issue.getTeamMember().getMember().getNickname());

                    assertThat(i.labels())
                            .hasSize(1)
                            .first()
                            .satisfies(l -> {
                                assertThat(l.getId()).isEqualTo(label.getId());
                                assertThat(l.getName()).isEqualTo(label.getTitle());
                                assertThat(l.getColor()).isEqualTo(label.getColor());
                                assertThat(l.getDescription()).isEqualTo(label.getDescription());
                            });
                });

    }

    @DisplayName("사용자로부터 요청값을 받아 마일스톤을 업데이트 한다.")
    @Test
    void updateMilestone() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        OAuthToken oauthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oauthToken);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = createTeamMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(member.getNickname(), team);
        projectRepository.save(project);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        Milestone milestone = createMilestone(project, startDate, dueDate);
        milestoneRepository.save(milestone);

        var request = MilestoneUpdateServiceRequest.builder()
                .title("updatedTitle")
                .description("updatedDescription")
                .startDate(startDate)
                .dueDate(dueDate.plusDays(2))
                .build();

        given(milestoneUpdateService.updateMilestone(anyLong(), any()))
                .willReturn(
                        MilestoneResponse.builder()
                                .milestoneId(milestone.getId())
                                .title("updatedTitle")
                                .description("updatedDescription")
                                .creator("nickname")
                                .startDate(startDate)
                                .dueDate(dueDate.plusDays(2))
                                .isCompleted(false)
                                .build()
                );

        // When
        MilestoneResponse milestoneResponse = milestoneService.updateMilestone(member.getId(), team.getId(), project.getId(), milestone.getId(), request);

        // Then
        assertThat(milestoneResponse)
                .extracting(
                        MilestoneResponse::milestoneId,
                        MilestoneResponse::title,
                        MilestoneResponse::description,
                        MilestoneResponse::dueDate,
                        MilestoneResponse::startDate
                )
                .containsExactlyInAnyOrder(
                        milestone.getId(),
                        "updatedTitle",
                        "updatedDescription",
                        dueDate.plusDays(2),
                        startDate
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

    private TeamMember createTeamMember(Member member, Team team) {
        return TeamMember.builder()
                .member(member)
                .team(team)
                .position(Position.NONE)
                .role(Role.ROLE_MEMBER)
                .build();
    }

    private OAuthToken createOAuthToken(Member member) {
        return OAuthToken.builder()
                .memberId(member.getId())
                .token("token")
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

    private MilestoneResponse createMilestoneResponse(Long milestoneId, LocalDateTime startDate, LocalDateTime dueDate) {
        return MilestoneResponse.builder()
                .milestoneId(milestoneId)
                .title("Test")
                .description("Description")
                .creator("nickname")
                .startDate(startDate)
                .dueDate(dueDate)
                .isCompleted(false)
                .build();
    }

    private Issue createIssue(String title, String content, TeamMember teamMember, Milestone milestone, Project project) {
        return Issue.builder()
                .title(title)
                .content(content)
                .teamMember(teamMember)
                .status(IssueStatus.OPEN)
                .githubIssueNumber(1L)
                .milestone(milestone)
                .project(project)
                .build();
    }

    private IssueLabel createLabel(String title, String color, String description, Team team, Project project) {
        return IssueLabel.builder()
                .title(title)
                .color(color)
                .description(description)
                .team(team)
                .project(project)
                .build();
    }

    private IssueLabelRelation createRelation(Issue issue, IssueLabel label) {
        return IssueLabelRelation.builder()
                .issue(issue)
                .issueLabel(label)
                .build();
    }

    private IssueDetailResponse createIssues(Issue issue, IssueLabel label) {
        return IssueDetailResponse.builder()
                .issueId(issue.getId())
                .title(issue.getTitle())
                .content(issue.getContent())
                .creator(issue.getTeamMember().getMember().getNickname())
                .labels(createIssueLabels(label))
                .build();
    }

    private List<IssueLabelDetailResponse> createIssueLabels(IssueLabel label) {
        return List.of(
                IssueLabelDetailResponse.builder()
                        .id(label.getId())
                        .name(label.getTitle())
                        .color(label.getColor())
                        .description(label.getDescription())
                        .build()
        );
    }

}
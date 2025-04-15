package soon.capstone.domain.issue.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.issue.entity.Issue;
import soon.capstone.domain.issue.entity.IssueStatus;
import soon.capstone.domain.issue.repository.issue.IssueRepository;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;
import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.entity.common.Position;
import soon.capstone.domain.teammember.entity.common.Role;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.common.UnauthorizedException;
import soon.capstone.infrastructure.github.service.dto.GithubIssueCreateServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueDetailListServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueDetailServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueUpdateServiceRequest;
import soon.capstone.infrastructure.github.service.dto.response.GithubIssueDetailResponse;
import soon.capstone.infrastructure.github.service.issue.GithubIssueService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static soon.capstone.global.exception.dto.ErrorDetail.UNAUTHORIZED;

class IssueServiceTest extends IntegrationTestSupport {

    @Autowired
    private IssueService issueService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private IssueLabelRelationService issueLabelRelationService;

    @MockitoBean
    private GithubIssueService githubIssueService;

    @MockitoBean
    private AssigneeService assigneeService;

    @AfterEach
    void tearDown() {
        Cache cache = cacheManager.getCache("issueDetail");
        if (cache != null) {
            cache.clear();
        }

        issueRepository.deleteAllInBatch();
        milestoneRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("이슈를 생성한다.")
    @Test
    void createIssue() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = createTeamMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        Milestone milestone = createMilestone(project);
        milestoneRepository.save(milestone);

        given(assigneeService.isAssigneeValid(anyLong(), anyString(), anyString(), anyString()))
            .willReturn(true);

        given(githubIssueService.createGithubIssue(any(GithubIssueCreateServiceRequest.class)))
            .willReturn(1L);

        // when
        Long issueId = issueService.create(
            member.getId(),
            team.getOrganizationName(),
            team.getName(),
            "title",
            "content",
            "assignees",
            List.of("label1", "label2"),
            milestone,
            project,
            teamMember
        );

        // then
        Issue issue = issueRepository.findById(issueId);
        assertThat(issue)
            .extracting("title", "content", "githubIssueNumber", "status")
            .contains("title", "content", 1L, IssueStatus.OPEN);

        verify(issueLabelRelationService, times(1))
            .linkIssueWithLabels(
                any(Issue.class),
                anyList(),
                anyLong(),
                anyString(),
                anyString()
            );
    }

    @Test
    @DisplayName("유효하지 않은 Assignee로 Issue 생성 시 예외가 발생한다.")
    void createIssueWithInvalidAssignee() {
        // given
        Long memberId = 1L;
        String organizationName = "org";
        String repositoryName = "repo";
        String assignees = "invalidAssignee";

        given(assigneeService.isAssigneeValid(memberId, organizationName, repositoryName, assignees))
            .willReturn(false);

        // expected
        assertThatThrownBy(() ->
            issueService.create(
                memberId, organizationName, repositoryName, "title", "content", assignees, List.of(), null, null, null
            )
        )
            .isInstanceOf(UnauthorizedException.class)
            .hasMessage(UNAUTHORIZED.getMessage());

        verifyNoInteractions(githubIssueService, issueLabelRelationService);
    }

    @DisplayName("이슈의 상태를 CLOSED로 변경한다")
    @Test
    void closedIssue() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = createTeamMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        Milestone milestone = createMilestone(project);
        milestoneRepository.save(milestone);

        Issue issue = Issue.createNewIssue("title", "content", 1L, teamMember, milestone, project);
        issueRepository.save(issue);

        // when
        issueService.closedIssue(member.getId(), issue.getId(), team.getOrganizationName(), project.getTitle());

        // then
        Issue closedIssue = issueRepository.findById(issue.getId());
        assertThat(closedIssue.getStatus())
            .isEqualTo(IssueStatus.CLOSED);

        verify(githubIssueService, times(1))
            .updateGithubIssue(any(GithubIssueUpdateServiceRequest.class));
    }

    @DisplayName("이슈 상세 정보를 정상적으로 반환한다.")
    @Test
    void retrievesIssueDetailSuccessfully() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = createTeamMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        Milestone milestone = createMilestone(project);
        milestoneRepository.save(milestone);

        Issue issue = Issue.createNewIssue("title", "content", 1L, teamMember, milestone, project);
        issueRepository.save(issue);

        GithubIssueDetailResponse githubResponse = createGithubIssueDetailResponse("title");

        given(githubIssueService.getIssueDetail(any(GithubIssueDetailServiceRequest.class)))
            .willReturn(githubResponse);

        List<IssueLabelDetailResponse> labels = List.of(
            createIssueLabelDetailResponse(1L, "label1"),
            createIssueLabelDetailResponse(2L, "label2")
        );
        given(issueLabelRelationService.findByLabelsByIssueId(any(Issue.class)))
            .willReturn(labels);

        // when
        IssueDetailResponse response = issueService.getIssueDetail(
            member.getId(),
            issue.getId(),
            team.getOrganizationName(),
            project.getTitle()
        );

        // then
        assertThat(response)
            .extracting("issueId", "title", "content", "creator", "status")
            .containsExactly(issue.getId(), "title", "body", "assignee", "open");

        assertThat(response.labels()).hasSize(2)
            .extracting("labelId", "name")
            .containsExactlyInAnyOrder(
                tuple(1L, "label1"),
                tuple(2L, "label2")
            );

        verify(issueLabelRelationService, times(1))
            .findByLabelsByIssueId(any(Issue.class));
    }

    @DisplayName("프로젝트의 모든 이슈를 조회한다.")
    @Test
    void getIssuesWithRepository() {
        // given
        Long memberId = 1L;
        String organizationName = "org";
        String repositoryName = "repo";

        given(githubIssueService.getIssuesWithRepository(any(GithubIssueDetailListServiceRequest.class)))
            .willReturn(List.of(
                createGithubIssueDetailResponse("title1"),
                createGithubIssueDetailResponse("title2")
            ));

        // when
        List<IssueDetailResponse> responses = issueService.getIssuesWithRepository(
            memberId,
            organizationName,
            repositoryName
        );

        // then
        assertThat(responses)
            .extracting("title")
            .containsExactlyInAnyOrder("title1", "title2");
    }

    private static GithubIssueDetailResponse createGithubIssueDetailResponse(String title) {
        return GithubIssueDetailResponse.builder()
            .assignee(Map.of("login", "assignee"))
            .body("body")
            .state("open")
            .title(title)
            .build();
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

    private Project createProject(Team team) {
        return Project.builder()
            .creator("creator")
            .title("title")
            .team(team)
            .repositoryId("repositoryId")
            .build();
    }

    private TeamMember createTeamMember(Member member, Team team) {
        return TeamMember.builder()
            .role(Role.ROLE_LEADER)
            .position(Position.NONE)
            .member(member)
            .team(team)
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

    private IssueLabelDetailResponse createIssueLabelDetailResponse(long labelId, String name) {
        return IssueLabelDetailResponse.builder()
            .labelId(labelId)
            .color("color")
            .name(name)
            .description("description")
            .build();
    }

}
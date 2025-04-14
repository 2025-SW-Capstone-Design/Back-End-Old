package soon.capstone.domain.issue.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.issue.entity.Issue;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.issue.entity.IssueLabelRelation;
import soon.capstone.domain.issue.entity.IssueStatus;
import soon.capstone.domain.issue.repository.issue.IssueRepository;
import soon.capstone.domain.issue.repository.issueLabelRelation.IssueLabelRelationRepository;
import soon.capstone.domain.issue.repository.issuelabel.IssueLabelRepository;
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
import soon.capstone.infrastructure.github.service.issue.GithubIssueLabelService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IssueLabelRelationServiceTest extends IntegrationTestSupport {

    @Autowired
    private IssueLabelRelationService issueLabelRelationService;

    @Autowired
    private IssueLabelRelationRepository issueLabelRelationRepository;

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
    private IssueLabelRepository issueLabelRepository;

    @MockitoBean
    private GithubIssueLabelService githubIssueLabelService;

    @AfterEach
    void tearDown() {
        issueLabelRelationRepository.deleteAllInBatch();
        issueLabelRepository.deleteAllInBatch();
        issueRepository.deleteAllInBatch();
        milestoneRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("이슈와 라벨이 연결된다.")
    @Test
    void linkIssueWithLabels() {
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

        Issue issue = createIssue(milestone);
        issueRepository.save(issue);

        IssueLabel issueLabel1 = createIssueLabel(project, team, "title1");
        IssueLabel issueLabel2 = createIssueLabel(project, team, "title2");
        issueLabelRepository.saveAll(List.of(issueLabel1, issueLabel2));

        // when
        issueLabelRelationService.linkIssueWithLabels(
            issue,
            List.of("title1", "title2"),
            member.getId(),
            "organizationName",
            "repositoryName"
        );

        // then
        List<IssueLabelRelation> relations = issueLabelRelationRepository.findAllByIssue(issue);
        assertThat(relations).hasSize(2);
    }

    @DisplayName("빈 라벨 목록으로 연결 시 작업을 수행하지 않는다.")
    @Test
    void linkIssueWithLabelsWithEmptyList() {
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

        Issue issue = createIssue(milestone);
        issueRepository.save(issue);

        // when
        issueLabelRelationService.linkIssueWithLabels(
            issue,
            List.of(),
            member.getId(),
            "organizationName",
            "repositoryName"
        );

        // then
        List<IssueLabelRelation> relations = issueLabelRelationRepository.findAllByIssue(issue);
        assertThat(relations).isEmpty();
    }

    @DisplayName("이슈 라벨 업데이트 시 기존 관계를 삭제하고 새로운 관계를 추가한다.")
    @Test
    void updateIssueRelation() {
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

        Issue issue = createIssue(milestone);
        issueRepository.save(issue);

        IssueLabel issueLabel1 = createIssueLabel(project, team, "label1");
        IssueLabel issueLabel2 = createIssueLabel(project, team, "label2");
        IssueLabel issueLabel3 = createIssueLabel(project, team, "label3");
        issueLabelRepository.saveAll(List.of(issueLabel1, issueLabel2, issueLabel3));

        IssueLabelRelation relation = IssueLabelRelation.createMapping(issue, issueLabel1);
        issueLabelRelationRepository.save(relation);

        // when
        issueLabelRelationService.updateIssueRelation(issue, List.of("label2", "label3"));

        // then
        List<IssueLabelRelation> relations = issueLabelRelationRepository.findAllByIssue(issue);
        assertThat(relations)
            .hasSize(2)
            .extracting(r -> r.getIssueLabel().getTitle())
            .containsExactlyInAnyOrder("label2", "label3");
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

    private Issue createIssue(Milestone milestone) {
        return Issue.builder()
            .title("title")
            .content("content")
            .githubIssueNumber(1L)
            .status(IssueStatus.OPEN)
            .milestone(milestone)
            .build();
    }

    private IssueLabel createIssueLabel(Project project, Team team, String title) {
        return IssueLabel.builder()
            .title(title)
            .description("description")
            .team(team)
            .project(project)
            .color("color")
            .build();
    }

}
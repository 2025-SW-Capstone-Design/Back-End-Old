package soon.capstone.domain.milestone.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.issue.entity.Issue;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.issue.entity.IssueLabelRelation;
import soon.capstone.domain.issue.entity.IssueStatus;
import soon.capstone.domain.issue.repository.issue.IssueRepository;
import soon.capstone.domain.issue.repository.issueLabelRelation.IssueLabelRelationRepository;
import soon.capstone.domain.issue.repository.issuelabel.IssueLabelRepository;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.milestone.service.dto.response.MilestoneDetailResponse;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
class MilestoneReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private MilestoneReadService milestoneReadService;

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

    @Autowired
    private IssueLabelRepository issueLabelRepository;

    @Autowired
    private IssueLabelRelationRepository issueLabelRelationRepository;

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

    @DisplayName("프로젝트에 속한 마일스톤들을 반환한다.")
    @Test
    void getMilestonesByProject() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

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

        // When
        List<MilestoneResponse> milestones = milestoneReadService.getMilestonesByProject(project);

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

        // When
        List<MilestoneResponse> milestones = milestoneReadService.getMilestonesByTeam(team);

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

        Issue issue1 = createIssue("Issue 1", "Content 1", teamMember, milestone, project);
        Issue issue2 = createIssue("Issue 2", "Content 2", teamMember, milestone, project);
        issueRepository.saveAll(List.of(issue1, issue2));

        IssueLabel label1 = createLabel("Label 1", "#FF0000", "Red", team, project);
        IssueLabel label2 = createLabel("Label 2", "#00FF00", "Green", team, project);
        issueLabelRepository.saveAll(List.of(label1, label2));

        IssueLabelRelation rel1 = createRelation(issue1, label1);
        IssueLabelRelation rel2 = createRelation(issue2, label1);
        IssueLabelRelation rel3 = createRelation(issue2, label2);
        issueLabelRelationRepository.saveAll(List.of(rel1, rel2, rel3));

        // When
        MilestoneDetailResponse response = milestoneReadService.getMilestoneDetail(milestone.getId());

        // Then
        assertThat(response.title()).isEqualTo("Test");
        assertThat(response.issues()).hasSize(2);

        assertThat(response.issues())
                .extracting("title", "content", "creator")
                .containsExactlyInAnyOrder(
                        tuple("Issue 1", "Content 1", "nickname"),
                        tuple("Issue 2", "Content 2", "nickname")
                );

        IssueDetailResponse issueWithTwoLabels = response.issues().stream()
                .filter(i -> i.title().equals("Issue 2"))
                .findFirst()
                .orElseThrow();

        assertThat(issueWithTwoLabels.labels()).hasSize(2);
        assertThat(issueWithTwoLabels.labels())
                .extracting("name")
                .containsExactlyInAnyOrder("Label 1", "Label 2");
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

    private TeamMember createTeamMember(Member member, Team team) {
        return TeamMember.builder()
                .member(member)
                .team(team)
                .position(Position.NONE)
                .role(Role.ROLE_MEMBER)
                .build();
    }

    private Issue createIssue(String title, String content, TeamMember teamMember, Milestone milestone, Project project) {
        return Issue.builder()
                .title(title)
                .content(content)
                .teamMember(teamMember)
                .status(IssueStatus.OPEN)
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

}
package soon.capstone.domain.milestone.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.milestone.service.dto.MilestoneMailDto;
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
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

class MilestoneListRepositoryImplTest extends IntegrationTestSupport {

    @Autowired
    private MilestoneListRepositoryImpl milestoneListRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @AfterEach
    void tearDown() {
        milestoneRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("마감일이 하루 남은 마일스톤을 조회한다.")
    @Test
    void getEmailsByMilestones() {
        // Given
        Member member1 = createMember("email1", "nickname1");
        Member member2 = createMember("email2", "nickname2");
        Member member3 = createMember("email3", "nickname3");
        memberRepository.saveAll(List.of(member1, member2, member3));

        Team team1 = createTeam("team1", "organization1");
        Team team2 = createTeam("team2", "organization2");
        teamRepository.saveAll(List.of(team1, team2));

        TeamMember teamMember1 = createTeamMember(member1, team1);
        TeamMember teamMember2 = createTeamMember(member2, team1);
        TeamMember teamMember3 = createTeamMember(member3, team2);
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2, teamMember3));

        Project project1 = createProject("project1", "creator1", team1);
        Project project2 = createProject("project2", "creator2", team2);
        projectRepository.saveAll(List.of(project1, project2));

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startTime = today.toLocalDate().atStartOfDay();
        LocalDateTime dueDate = startTime.plusDays(1);

        Milestone milestone1 = createMilestone("milestone1", member1.getNickname(), project1, startTime, dueDate);
        Milestone milestone2 = createMilestone("milestone2", member2.getNickname(), project1, startTime, dueDate);
        Milestone milestone3 = createMilestone("milestone3", member3.getNickname(), project2, startTime, dueDate);
        Milestone milestone4 = createMilestone("milestone4", member3.getNickname(), project2, startTime, dueDate.plusDays(1));
        milestoneRepository.saveAll(List.of(milestone1, milestone2, milestone3, milestone4));

        // When
        List<MilestoneMailDto> responses = milestoneListRepository.getEmailsByMilestones();

        // Then
        assertThat(responses).hasSize(3)
            .extracting("milestoneTitle", "teamName", "emails")
            .containsExactlyInAnyOrder(
                tuple("milestone1", "team1", List.of("email1", "email2")),
                tuple("milestone2", "team1", List.of("email1", "email2")),
                tuple("milestone3", "team2", List.of("email3"))
            );
    }

    @DisplayName("마감일이 하루 남은 마일스톤이 없다면, 빈 리스트를 반환한다.")
    @Test
    void getEmailsByMilestonesWillReturnEmptyList() {
        // Given
        Member member1 = createMember("email1", "nickname1");
        Member member2 = createMember("email2", "nickname2");
        Member member3 = createMember("email3", "nickname3");
        memberRepository.saveAll(List.of(member1, member2, member3));

        Team team1 = createTeam("team1", "organization1");
        Team team2 = createTeam("team2", "organization2");
        teamRepository.saveAll(List.of(team1, team2));

        TeamMember teamMember1 = createTeamMember(member1, team1);
        TeamMember teamMember2 = createTeamMember(member2, team1);
        TeamMember teamMember3 = createTeamMember(member3, team2);
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2, teamMember3));

        Project project1 = createProject("project1", "creator1", team1);
        Project project2 = createProject("project2", "creator2", team2);
        projectRepository.saveAll(List.of(project1, project2));

        // When
        List<MilestoneMailDto> responses = milestoneListRepository.getEmailsByMilestones();

        // Then
        assertThat(responses).isEmpty();
    }

    private Member createMember(String email, String nickname) {
        return Member.builder()
            .email(email)
            .nickname(nickname)
            .profileImageURL("profileImageURL")
            .build();
    }

    private Team createTeam(String name, String organizationName) {
        return Team.builder()
            .name(name)
            .description("description")
            .organizationName(organizationName)
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

    private Project createProject(String title, String creator, Team team) {
        return Project.builder()
            .title(title)
            .repositoryId("repositoryId")
            .creator(creator)
            .team(team)
            .build();
    }

    private Milestone createMilestone(String title, String creator, Project project, LocalDateTime startTime, LocalDateTime dueDate) {
        return Milestone.builder()
            .title(title)
            .description("Description")
            .creator(creator)
            .startDate(startTime)
            .dueDate(dueDate)
            .project(project)
            .build();
    }

}
package soon.capstone.domain.milestone.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
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

    @MockitoBean
    private MilestoneCreationService milestoneCreationService;

    @MockitoBean
    private MilestoneReadService milestoneReadService;

    @AfterEach
    void tearDown() {
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
                .projectId(project.getId())
                .teamId(team.getId())
                .build();

        // When
        milestoneService.createMilestone(memberId, request);

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

}
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
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.milestone.entity.MilestoneStatus;
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.milestone.service.dto.MilestoneUpdateDto;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.milestone.MilestoneInvalidDateException;
import soon.capstone.infrastructure.github.service.milestone.GithubMilestoneUpdateService;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest
class MilestoneUpdateServiceTest extends IntegrationTestSupport {

    @Autowired
    private MilestoneUpdateService milestoneUpdateService;

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
    private GithubMilestoneUpdateService githubMilestoneUpdateService;

    @AfterEach
    void tearDown() {
        milestoneRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("사용자로부터 요청값을 받아 마일스톤을 업데이트 한다.")
    @Test
    void updateMilestone() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        OAuthToken oauthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oauthToken);

        Project project = createProject(member.getNickname(), team);
        projectRepository.save(project);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        Milestone milestone = Milestone.builder()
            .title("title")
            .description("description")
            .dueDate(dueDate)
            .creator(member.getNickname())
            .startDate(startDate)
            .githubMilestoneId(1)
            .project(project)
            .status(MilestoneStatus.NOT_STARTED)
            .build();
        milestoneRepository.save(milestone);

        Long milestoneId = milestone.getId();
        MilestoneUpdateDto milestoneUpdateDto = MilestoneUpdateDto.of(
            team.getOrganizationName(),
            project.getTitle(),
            oauthToken.getToken(),
            "updatedTitle",
            "updatedDescription",
            dueDate.plusDays(1),
            startDate
        );

        // When
        MilestoneResponse milestoneResponse = milestoneUpdateService.updateMilestone(milestoneId, milestoneUpdateDto);

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
                milestoneId,
                "updatedTitle",
                "updatedDescription",
                dueDate.plusDays(1),
                startDate
            );
    }

    @DisplayName("마일스톤을 업데이트 할 때, 시작일과 마감일이 유효하지 않으면 예외가 발생한다.")
    @Test
    void updateMilestone_WhenDateIsInvalid_ThrowsMilestoneInvalidDateException() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        OAuthToken oauthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oauthToken);

        Project project = createProject(member.getNickname(), team);
        projectRepository.save(project);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        Milestone milestone = Milestone.builder()
            .title("title")
            .description("description")
            .dueDate(dueDate)
            .creator(member.getNickname())
            .startDate(startDate)
            .githubMilestoneId(1)
            .project(project)
            .status(MilestoneStatus.NOT_STARTED)
            .build();
        milestoneRepository.save(milestone);

        Long milestoneId = milestone.getId();
        MilestoneUpdateDto milestoneUpdateDto = MilestoneUpdateDto.of(
            team.getOrganizationName(),
            project.getTitle(),
            oauthToken.getToken(),
            "updatedTitle",
            "updatedDescription",
            dueDate,
            startDate.plusDays(8)
        );

        // Expect
        assertThatThrownBy(() -> milestoneUpdateService.updateMilestone(milestoneId, milestoneUpdateDto))
            .isInstanceOf(MilestoneInvalidDateException.class)
            .hasMessage("마일스톤의 시작일과 종료일이 올바르지 않습니다.");
    }

    @DisplayName("마일스톤의 상태가 DONE일 때 깃허브의 상태를 업데이트한다.")
    @Test
    void updateMilestoneStatus() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        OAuthToken oauthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oauthToken);

        Project project = createProject(member.getNickname(), team);
        projectRepository.save(project);

        Milestone milestone = Milestone.builder()
            .title("title")
            .description("description")
            .dueDate(LocalDateTime.now().plusDays(7))
            .creator(member.getNickname())
            .startDate(LocalDateTime.now())
            .githubMilestoneId(1)
            .project(project)
            .status(MilestoneStatus.NOT_STARTED)
            .build();
        milestoneRepository.save(milestone);

        // when
        milestoneUpdateService.updateMilestoneStatus(
            milestone.getId(),
            MilestoneStatus.DONE,
            project,
            team,
            oauthToken.getToken()
        );

        // then
        Milestone updatedMilestone = milestoneRepository.findById(milestone.getId());
        assertThat(updatedMilestone.getStatus()).isEqualTo(MilestoneStatus.DONE);

        verify(githubMilestoneUpdateService).updateMilestoneStatus(
            team.getOrganizationName(),
            project.getTitle(),
            updatedMilestone.getGithubMilestoneId(),
            oauthToken.getToken(),
            "closed"
        );
    }

    @DisplayName("마일스톤의 상태가 DONE이 아니라면 깃허브 상태를 업데이트하지 않는다.")
    @Test
    void updateMilestoneStatusDoesNotGithubState() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        OAuthToken oauthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oauthToken);

        Project project = createProject(member.getNickname(), team);
        projectRepository.save(project);

        Milestone milestone = Milestone.builder()
            .title("title")
            .description("description")
            .dueDate(LocalDateTime.now().plusDays(7))
            .creator(member.getNickname())
            .startDate(LocalDateTime.now())
            .githubMilestoneId(1)
            .project(project)
            .status(MilestoneStatus.NOT_STARTED)
            .build();
        milestoneRepository.save(milestone);

        // when
        milestoneUpdateService.updateMilestoneStatus(
            milestone.getId(),
            MilestoneStatus.IN_PROGRESS,
            project,
            team,
            oauthToken.getToken()
        );

        // then
        Milestone updatedMilestone = milestoneRepository.findById(milestone.getId());
        assertThat(updatedMilestone.getStatus()).isEqualTo(MilestoneStatus.IN_PROGRESS);

        verifyNoInteractions(githubMilestoneUpdateService);
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

}
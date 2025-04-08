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
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.milestone.service.dto.MilestoneCreationDto;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.milestone.MilestoneInvalidDateException;
import soon.capstone.infrastructure.github.service.milestone.GithubMilestoneCreationService;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MilestoneCreationServiceTest extends IntegrationTestSupport {

    @Autowired
    private MilestoneCreationService milestoneCreationService;

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
    private GithubMilestoneCreationService githubMilestoneCreationService;

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

        OAuthToken oauthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oauthToken);

        Project project = createProject(member.getNickname(), team);
        projectRepository.save(project);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(7);

        MilestoneCreationDto milestoneCreationDto = createMilestoneCreationDto(team, project, oauthToken, dueDate, startDate, member);

        // When
        milestoneCreationService.createMilestone(milestoneCreationDto);

        // Then
        Long milestoneId = 1L;
        Milestone milestone = milestoneRepository.findById(milestoneId);

        assertThat(milestone)
                .extracting(
                        Milestone::getCreator,
                        Milestone::getTitle,
                        Milestone::getDescription,
                        Milestone::getDueDate,
                        Milestone::getStartDate
                )
                .containsExactly(member.getNickname(), "title", "description", dueDate, startDate);
    }

    @DisplayName("마일스톤 생성 시, 시작일과 마감일이 유효하지 않으면 예외가 발생한다.")
    @Test
    void createMilestone_WhenDateIsInvalid_ThrowsMilestoneInvalidDateException() {
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
        LocalDateTime dueDate = startDate.minusDays(1);

        MilestoneCreationDto milestoneCreationDto = createMilestoneCreationDto(team, project, oauthToken, dueDate, startDate, member);

        // Expect
        assertThatThrownBy(() -> milestoneCreationService.createMilestone(milestoneCreationDto))
                .isInstanceOf(MilestoneInvalidDateException.class)
                .hasMessage("마일스톤의 시작일과 종료일이 올바르지 않습니다.");
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

    private MilestoneCreationDto createMilestoneCreationDto(Team team, Project project, OAuthToken oauthToken, LocalDateTime dueDate, LocalDateTime startDate, Member member) {
        return MilestoneCreationDto.builder()
                .owner(team.getOrganizationName())
                .repo(project.getTitle())
                .oauthToken(oauthToken.getToken())
                .title("title")
                .description("description")
                .dueDate(dueDate)
                .startDate(startDate)
                .creator(member.getNickname())
                .project(project)
                .build();
    }

}
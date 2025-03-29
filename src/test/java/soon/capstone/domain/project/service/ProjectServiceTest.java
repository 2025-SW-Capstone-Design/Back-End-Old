package soon.capstone.domain.project.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.project.service.dto.request.TeamCreatedEvent;
import soon.capstone.domain.project.service.dto.response.ProjectDetailResponse;
import soon.capstone.domain.project.service.dto.response.RepositoryCreationEvent;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.entity.common.Position;
import soon.capstone.domain.teammember.entity.common.Role;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class ProjectServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OAuthTokenRepository oAuthTokenRepository;

    @Autowired
    private ProjectService projectService;

    @MockitoBean
    private RepositoryCreationService repositoryCreationService;

    @MockitoBean
    private OrganizationProjectCreationService organizationProjectCreationService;

    @MockitoBean
    private ProjectReadService projectReadService;

    @AfterEach
    void tearDown() {
        projectRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("사용자가 속한 팀의 프로젝트를 조회한다.")
    @Test
    void getProjects() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        OAuthToken oAuthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oAuthToken);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = createTeamMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(member.getNickname(), team);
        projectRepository.save(project);

        given(projectReadService.getProjects(any(), any()))
                .willReturn(List.of(createProjectDetailResponse(project)));

        // When
        var projects = projectService.getProjects(member.getId(), team.getId());

        // Then
        assertThat(projects).hasSize(1);
    }

    @DisplayName("팀 생성 이벤트가 발생하여, 레포지토리 생성 서비스가 호출된다.")
    @Test
    void createRepository() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        OAuthToken oAuthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oAuthToken);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = createTeamMember(member, team);
        teamMemberRepository.save(teamMember);

        var teamCreatedEvent = TeamCreatedEvent.builder()
                .memberId(member.getId())
                .teamId(team.getId())
                .oauthToken(oAuthToken.getToken())
                .build();

        // When
        projectService.createRepository(teamCreatedEvent);

        // Then
        verify(repositoryCreationService).createRepository(any(), anyString(), anyString());
    }

    @DisplayName("레포지토리 생성 이벤트가 발생하면, 프로젝트 생성 서비스가 호출된다.")
    @Test
    void createProject() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        OAuthToken oAuthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oAuthToken);

        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(member.getNickname(), team);
        projectRepository.save(project);

        var repositoryCreationEvent = RepositoryCreationEvent.builder()
                .oauthToken(oAuthToken.getToken())
                .organizationName(team.getOrganizationName())
                .build();

        // When
        projectService.createProject(repositoryCreationEvent);

        // Then
        verify(organizationProjectCreationService).createProject(anyString(), anyString());
    }

    private Member createMember() {
        return Member.builder()
                .email("email")
                .nickname("nickname")
                .profileImageURL("profileImageURL")
                .build();
    }

    private OAuthToken createOAuthToken(Member member) {
        return OAuthToken.builder()
                .memberId(member.getId())
                .token("token")
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

    private Project createProject(String creator, Team team) {
        return Project.builder()
                .title("title")
                .creator(creator)
                .team(team)
                .build();
    }

    private ProjectDetailResponse createProjectDetailResponse(Project project) {
        return ProjectDetailResponse.builder()
                .projectId(project.getId())
                .title(project.getTitle())
                .creator(project.getCreator())
                .build();
    }
}
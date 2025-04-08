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
import soon.capstone.domain.milestone.service.dto.request.MilestoneCreateServiceRequest;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.entity.common.Position;
import soon.capstone.domain.teammember.entity.common.Role;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.milestone.MilestoneInvalidDateException;
import soon.capstone.infrastructure.github.service.milestone.GithubMilestoneCreationService;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

}
package soon.capstone.domain.issue.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.issue.repository.issuelabel.IssueLabelRepository;
import soon.capstone.domain.issue.service.dto.request.IssueLabelCreateServiceRequest;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectJpaRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.team.TeamNotAuthorizedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static soon.capstone.global.exception.dto.ErrorDetail.TEAM_NOT_AUTHORIZED;

class IssueManagementServiceTest extends IntegrationTestSupport {

    @Autowired
    private IssueManagementService issueManagementService;

    @Autowired
    private IssueLabelRepository issueLabelRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProjectJpaRepository projectJpaRepository; // TODO: ProjectRepository로 변경

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @MockitoBean
    private IssueLabelService issueLabelService;

    @AfterEach
    void tearDown() {
        issueLabelRepository.deleteAllInBatch();
        projectJpaRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("이슈 라벨을 생성한다.")
    @Test
    void createIssueLabel() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        var request = createIssueLabelCreateServiceRequest(team, project);

        given(issueLabelService.createIssueLabel(
            anyString(),
            anyString(),
            anyString(),
            any(Project.class),
            anyLong(),
            any(Team.class)
        )).willReturn(1L);

        // when
        Long issueLabelId = issueManagementService.createIssueLabel(request, member.getId());

        // then
        assertThat(issueLabelId)
            .isEqualTo(1L);
    }

    @DisplayName("팀에 속하지 않은 멤버가 이슈 라벨을 생성 할 경우 예외가 발생한다")
    @Test
    void createIssueLabelWithNotTeamMember() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectJpaRepository.save(project);

        // expected
        assertThatThrownBy(() -> {
            issueManagementService.createIssueLabel(
                createIssueLabelCreateServiceRequest(team, project),
                member.getId()
            );
        }).isInstanceOf(TeamNotAuthorizedException.class)
            .hasMessage(TEAM_NOT_AUTHORIZED.getMessage());
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
            .organizationName("organizationName")
            .name("teamName")
            .description("teamDescription")
            .build();
    }

    private Project createProject(Team team) {
        return Project.builder()
            .creator("creator")
            .title("title")
            .team(team)
            .build();
    }

    private IssueLabelCreateServiceRequest createIssueLabelCreateServiceRequest(Team team, Project project) {
        return IssueLabelCreateServiceRequest.builder()
            .color("color")
            .teamId(team.getId())
            .title("title")
            .description("description")
            .projectId(project.getId())
            .build();
    }

}
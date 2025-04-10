package soon.capstone.domain.readme.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.readme.entity.Readme;
import soon.capstone.domain.readme.repository.ReadmeRepository;
import soon.capstone.domain.readme.service.dto.request.ReadmeCreateServiceRequest;
import soon.capstone.domain.readme.service.dto.request.ReadmeDeleteServiceRequest;
import soon.capstone.domain.readme.service.dto.request.ReadmeUpdateServiceRequest;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.readme.ReadmeNotFoundException;
import soon.capstone.global.exception.team.TeamNotAuthorizedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static soon.capstone.global.exception.dto.ErrorDetail.READEME_NOT_FOUND;
import static soon.capstone.global.exception.dto.ErrorDetail.TEAM_NOT_AUTHORIZED;

class ReadmeServiceTest extends IntegrationTestSupport {

    @Autowired
    private ReadmeService readmeService;

    @Autowired
    private ReadmeRepository readmeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @AfterEach
    void tearDown() {
        readmeRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
    }

    @DisplayName("신규 리드미를 생성한다. 버전은 가장 최근의 리드미에서 1증가한 값이다.")
    @Test
    void createNewReadmeWithIncrementedVersion() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        Readme readme = Readme.createNew("title", "content", 1, member, project);
        readmeRepository.save(readme);

        ReadmeCreateServiceRequest request = createReadmeCreateServiceRequest(project, team, member);

        // when
        Long readmeId = readmeService.create(request);

        // then
        Readme createdReadme = readmeRepository.findById(readmeId);
        assertThat(createdReadme)
            .extracting("version", "isLatest")
            .containsExactly(2, true);
    }

    @DisplayName("신규 리드미 생성시 버전은 1이다.")
    @Test
    void createReadmeWhenReadmeIsEmpty() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        ReadmeCreateServiceRequest request = createReadmeCreateServiceRequest(project, team, member);

        // when
        Long readmeId = readmeService.create(request);

        // then
        Readme readme = readmeRepository.findById(readmeId);
        assertThat(readme.getVersion())
            .isOne();
    }

    @DisplayName("기존 리드미가 있을 때, 새로운 리드미 생성 시 이전 리드미의 최신 상태가 해제된다")
    @Test
    void createNewVersionReadmeAndMarkOldReadmeAsNotLatest() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        Readme oldReadme = Readme.createNew("title", "content", 0, member, project);
        readmeRepository.save(oldReadme);

        ReadmeCreateServiceRequest request = createReadmeCreateServiceRequest(project, team, member);

        // when
        Long newReadmeId = readmeService.create(request);

        // then
        Readme newReadme = readmeRepository.findById(newReadmeId);
        Readme oldReadmeAfterUpdate = readmeRepository.findById(oldReadme.getId());

        assertThat(newReadme.isLatest())
            .isTrue();
        assertThat(oldReadmeAfterUpdate.isLatest())
            .isFalse();
    }

    @DisplayName("팀 멤버가 아닌 사용자가 리드미 생성 시 예외가 발생한다")
    @Test
    void throwExceptionWhenNonTeamMemberCreatesReadme() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectRepository.save(project);

        ReadmeCreateServiceRequest request = createReadmeCreateServiceRequest(project, team, member);

        // expect
        assertThatThrownBy(() -> readmeService.create(request))
            .isInstanceOf(TeamNotAuthorizedException.class)
            .hasMessage(TEAM_NOT_AUTHORIZED.getMessage());
    }

    @DisplayName("리드미를 수정한다.")
    @Test
    void updateReadme() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        Readme oldReadme = Readme.createNew("title", "content", 0, member, project);
        readmeRepository.save(oldReadme);

        ReadmeUpdateServiceRequest request = ReadmeUpdateServiceRequest.builder()
            .readmeId(oldReadme.getId())
            .title("new title")
            .content("new Content")
            .memberId(member.getId())
            .teamId(team.getId())
            .projectId(project.getId())
            .build();

        // when
        Long readmeId = readmeService.update(request);

        // then
        Readme newReadme = readmeRepository.findById(readmeId);
        assertThat(newReadme)
            .extracting("title", "content", "version", "isLatest")
            .containsExactlyInAnyOrder("new title", "new Content", oldReadme.getVersion() + 1, true);
    }

    @DisplayName("리드미를 삭제한다.")
    @Test
    void deleteReadme() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        Readme readme = Readme.createNew("title", "content", 1, member, project);
        readmeRepository.save(readme);

        ReadmeDeleteServiceRequest request = ReadmeDeleteServiceRequest.builder()
            .readmeId(readme.getId())
            .teamId(team.getId())
            .memberId(member.getId())
            .build();

        // when
        readmeService.delete(request);

        // expected
        assertThatThrownBy(() -> readmeRepository.findById(request.readmeId()))
            .isInstanceOf(ReadmeNotFoundException.class)
            .hasMessage(READEME_NOT_FOUND.getMessage());
    }

    @DisplayName("팀 멤버가 아닌 사용자가 리드미 삭제 시 예외가 발생한다")
    @Test
    void throwExceptionWhenNonTeamMemberDeletesReadme() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        Project project = createProject(team);
        projectRepository.save(project);

        Readme readme = Readme.createNew("title", "content", 1, member, project);
        readmeRepository.save(readme);

        ReadmeDeleteServiceRequest request = ReadmeDeleteServiceRequest.builder()
            .readmeId(readme.getId())
            .teamId(team.getId())
            .memberId(member.getId())
            .build();

        // expected
        assertThatThrownBy(() -> readmeService.delete(request))
            .isInstanceOf(TeamNotAuthorizedException.class)
            .hasMessage(TEAM_NOT_AUTHORIZED.getMessage());
    }

    @DisplayName("여러 버전의 리드미가 있을 때 최신 버전만 최신 상태로 표시된다")
    @Test
    void onlyLatestReadmeIsMarkedAsLatest() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember teamMember = TeamMember.createMember(member, team);
        teamMemberRepository.save(teamMember);

        Project project = createProject(team);
        projectRepository.save(project);

        // when
        ReadmeCreateServiceRequest request1 = createReadmeCreateServiceRequest(project, team, member);
        Long firstId = readmeService.create(request1);

        ReadmeCreateServiceRequest request2 = createReadmeCreateServiceRequest(project, team, member);
        Long secondId = readmeService.create(request2);

        ReadmeCreateServiceRequest request3 = createReadmeCreateServiceRequest(project, team, member);
        Long thirdId = readmeService.create(request3);

        // then
        Readme first = readmeRepository.findById(firstId);
        Readme second = readmeRepository.findById(secondId);
        Readme third = readmeRepository.findById(thirdId);

        assertThat(first.isLatest())
            .isFalse();
        assertThat(second.isLatest())
            .isFalse();
        assertThat(third.isLatest())
            .isTrue();
    }

    private ReadmeCreateServiceRequest createReadmeCreateServiceRequest(Project project, Team team, Member member) {
        return ReadmeCreateServiceRequest.builder()
            .title("title")
            .content("content")
            .projectId(project.getId())
            .teamId(team.getId())
            .memberId(member.getId())
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
            .repositoryId("repositoryId")
            .build();
    }

}
package soon.capstone.domain.project.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.infrastructure.github.service.GithubRepositoryCreationService;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class RepositoryCreationServiceTest extends IntegrationTestSupport {

    @Autowired
    private RepositoryCreationService repositoryCreationService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private OAuthTokenRepository oAuthTokenRepository;

    @MockitoBean
    private GithubRepositoryCreationService githubRepositoryCreationService;

    @DisplayName("팀 생성 후, 해당 팀에 자동으로 Github Repository가 생성된다.")
    @Test
    void createRepository() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        OAuthToken oauthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oauthToken);

        // When
        repositoryCreationService.createRepository(team.getId(), member.getNickname(), oauthToken.getToken());

        // Then
        List<Project> projects = projectRepository.findAll();
        List<String> expectedTitles = List.of(
                String.format("%s-Frontend", team.getOrganizationName()),
                String.format("%s-Backend", team.getOrganizationName())
        );

        assertThat(projects.size()).isEqualTo(2);
        assertThat(projects)
                .extracting(Project::getTitle)
                .containsExactly(expectedTitles.get(0), expectedTitles.get(1));

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

}
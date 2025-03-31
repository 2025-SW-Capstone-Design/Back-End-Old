package soon.capstone.domain.project.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.infrastructure.github.service.GithubLinkProjectToRepositoryService;
import soon.capstone.infrastructure.github.service.GithubOrganizationReadService;
import soon.capstone.infrastructure.github.service.GithubProjectCreationService;
import soon.capstone.infrastructure.redis.oauth2.entity.OAuthToken;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class OrganizationProjectCreationServiceTest extends IntegrationTestSupport {

    @Autowired
    private OrganizationProjectCreationService organizationProjectCreationService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private OAuthTokenRepository oAuthTokenRepository;

    @MockitoBean
    private GithubOrganizationReadService githubOrganizationReadService;

    @MockitoBean
    private GithubProjectCreationService githubProjectCreationService;

    @MockitoBean
    private GithubLinkProjectToRepositoryService githubLinkProjectToRepositoryService;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        oAuthTokenRepository.deleteAll();
    }

    @DisplayName("레포지토리 생성 후, 해당 팀에 자동으로 Github Project가 생성된다.")
    @Test
    void createProject() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        OAuthToken oauthToken = createOAuthToken(member);
        oAuthTokenRepository.save(oauthToken);

        String organizationName = team.getOrganizationName();
        String token = oauthToken.getToken();
        String mockOrgId = "mock-organization-id";
        String mockRepoId = "mock-repo-id";
        String mockRepoName = "mock-repo-name";

        given(githubOrganizationReadService.getOrganizationId(organizationName, token))
                .willReturn(mockOrgId);

        // When
        organizationProjectCreationService.createProject(organizationName, token, mockRepoId, mockRepoName);

        // Then
        verify(githubOrganizationReadService).getOrganizationId(organizationName, token);
        verify(githubProjectCreationService).createProject(mockOrgId, mockRepoName, token);
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
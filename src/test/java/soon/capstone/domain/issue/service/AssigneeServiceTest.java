package soon.capstone.domain.issue.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.infrastructure.github.service.dto.GithubAssigneesServiceRequest;
import soon.capstone.infrastructure.github.service.issue.GithubAssigneesService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AssigneeServiceTest extends IntegrationTestSupport {

    @Autowired
    private AssigneeService assigneeService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private GithubAssigneesService githubAssigneesService;

    @AfterEach
    void tearDown() {
        Cache cache = cacheManager.getCache("assignee");
        if (cache != null) {
            cache.clear();
        }
    }

    @DisplayName("유효한 Assignee 요청이 성공적으로 검증된다.")
    @Test
    void isAssigneeValidReturnsTrueForValidRequest() {
        // given
        Long memberId = 1L;
        String organizationName = "org";
        String repositoryName = "repo";
        String assignees = "validAssignee";

        var request = createGithubAssigneeServiceRequest(organizationName, repositoryName, assignees, memberId);
        given(githubAssigneesService.isAssignee(request))
            .willReturn(true);

        // when
        boolean result = assigneeService.isAssigneeValid(memberId, organizationName, repositoryName, assignees);

        // then
        assertThat(result)
            .isTrue();
    }

    @DisplayName("유효하지 않은 Assignee 요청이 실패로 검증된다.")
    @Test
    void isAssigneeValidReturnsFalseForInvalidRequest() {
        // given
        Long memberId = 1L;
        String organizationName = "org";
        String repositoryName = "repo";
        String assignees = "invalidAssignee";

        var request = createGithubAssigneeServiceRequest(organizationName, repositoryName, assignees, memberId);
        given(githubAssigneesService.isAssignee(request))
            .willReturn(false);

        // when
        boolean result = assigneeService.isAssigneeValid(memberId, organizationName, repositoryName, assignees);

        // then
        assertThat(result)
            .isFalse();
    }

    @DisplayName("Assignee 요청이 캐시에서 성공적으로 조회된다.")
    @Test
    void isAssigneeValidReturnsCachedValue() {
        // given
        Long memberId = 1L;
        String organizationName = "org";
        String repositoryName = "repo";
        String assignees = "cachedAssignee";

        var request = createGithubAssigneeServiceRequest(organizationName, repositoryName, assignees, memberId);
        given(githubAssigneesService.isAssignee(request))
            .willReturn(true);

        // when
        boolean firstCall = assigneeService.isAssigneeValid(memberId, organizationName, repositoryName, assignees);
        boolean secondCall = assigneeService.isAssigneeValid(memberId, organizationName, repositoryName, assignees);

        // then
        assertThat(firstCall).isTrue();
        assertThat(secondCall).isTrue();
        verify(githubAssigneesService, times(1))
            .isAssignee(request);
    }

    private GithubAssigneesServiceRequest createGithubAssigneeServiceRequest(String organizationName, String repositoryName, String assignees, Long memberId) {
        return GithubAssigneesServiceRequest.builder()
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .assignee(assignees)
            .memberId(memberId)
            .build();
    }

}
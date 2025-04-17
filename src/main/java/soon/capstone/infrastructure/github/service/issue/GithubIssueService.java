package soon.capstone.infrastructure.github.service.issue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import soon.capstone.global.exception.github.GithubHttpClientException;
import soon.capstone.infrastructure.github.service.dto.GithubIssueCreateServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueDetailListServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueDetailServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueUpdateServiceRequest;
import soon.capstone.infrastructure.github.service.dto.response.GithubIssueCreateResponse;
import soon.capstone.infrastructure.github.service.dto.response.GithubIssueDetailResponse;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;
import soon.capstone.infrastructure.restclient.config.RestClientConfig;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubIssueService {

    private static final String ISSUE_URL = "/repos/{organizationName}/{repositoryName}/issues";
    private static final String ISSUE_ORGANIZATION_URL = "orgs/{organizationName}/issues?state=all";

    private final RestClientConfig restClientConfig;
    private final OAuthTokenRepository oAuthTokenRepository;

    public Long createGithubIssue(GithubIssueCreateServiceRequest request) {
        try {
            RestClient restClient = getRestClient(request.memberId());

            String uri = ISSUE_URL
                .replace("{organizationName}", request.organizationName())
                .replace("{repositoryName}", request.repositoryName());

            return Objects.requireNonNull(restClient.post()
                    .uri(uri)
                    .body(request.toGithubRequest())
                    .retrieve()
                    .body(GithubIssueCreateResponse.class))
                .number();
        } catch (Exception e) {
            log.error("GitHub Issue 생성 중 오류 발생", e);
            throw new GithubHttpClientException();
        }
    }

    /**
     * GitHub Issue 수정
     *
     * @param request labels가 빈 List일 경우 이슈의 label이 모두 삭제됨
     *                assignees가 null일 경우 이슈의 assignee가 모두 삭제됨
     *                둘다 모두 이전의 내용을 포함하지 않을경우 유지되지 않음
     */
    public void updateGithubIssue(GithubIssueUpdateServiceRequest request) {
        try {
            RestClient restClient = getRestClient(request.memberId());
            String uri = (ISSUE_URL + "/{issueNumber}")
                .replace("{organizationName}", request.organizationName())
                .replace("{repositoryName}", request.repositoryName())
                .replace("{issueNumber}", String.valueOf(request.issueNumber()));

            restClient.patch()
                .uri(uri)
                .body(request.toGithubRequest())
                .retrieve()
                .body(Void.class);
        } catch (Exception e) {
            log.error("GitHub Issue 수정 중 오류 발생", e);
            throw new GithubHttpClientException();
        }
    }

    public GithubIssueDetailResponse getIssueDetail(GithubIssueDetailServiceRequest request) {
        try {
            RestClient restClient = getRestClient(request.memberId());
            String uri = (ISSUE_URL + "/{issueNumber}")
                .replace("{organizationName}", request.organizationName())
                .replace("{repositoryName}", request.repositoryName())
                .replace("{issueNumber}", String.valueOf(request.issueNumber()));

            return restClient.get()
                .uri(uri)
                .retrieve()
                .body(GithubIssueDetailResponse.class);
        } catch (Exception e) {
            log.error("GitHub Issue 조회 중 오류 발생", e);
            throw new GithubHttpClientException();
        }
    }

    public List<GithubIssueDetailResponse> getIssuesWithRepository(GithubIssueDetailListServiceRequest request) {
        try {
            RestClient restClient = getRestClient(request.memberId());
            String uri = ISSUE_URL
                .replace("{organizationName}", request.organizationName())
                .replace("{repositoryName}", request.repositoryName()) + "?state=all";

            return Objects.requireNonNull(restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<GithubIssueDetailResponse>>() {
                    }))
                .stream()
                .filter(GithubIssueDetailResponse::isPureIssue)
                .toList();
        } catch (Exception e) {
            log.error("GitHub Repository Issues 조회 중 오류 발생", e);
            throw new GithubHttpClientException();
        }
    }

    public List<GithubIssueDetailResponse> getIssuesWithOrganization(GithubIssueDetailListServiceRequest request) {
        try {
            RestClient restClient = getRestClient(request.memberId());
            String uri = ISSUE_ORGANIZATION_URL.replace("{organizationName}", request.organizationName());

            return Objects.requireNonNull(restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<GithubIssueDetailResponse>>() {
                    }))
                .stream()
                .filter(GithubIssueDetailResponse::isPureIssue)
                .toList();
        } catch (Exception e) {
            log.error("GitHub Organization Issues 조회 중 오류 발생", e);
            throw new GithubHttpClientException();
        }
    }

    private RestClient getRestClient(Long memberId) {
        String token = oAuthTokenRepository.findByMemberId(memberId).getToken();
        return restClientConfig.githubRestClient(token);
    }

}
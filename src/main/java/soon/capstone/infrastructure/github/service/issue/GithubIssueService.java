package soon.capstone.infrastructure.github.service.issue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;
import soon.capstone.global.exception.github.GithubHttpClientException;
import soon.capstone.infrastructure.github.service.dto.GithubIssueCreateServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueDetailServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueUpdateServiceRequest;
import soon.capstone.infrastructure.github.service.dto.response.GithubIssueCreateResponse;
import soon.capstone.infrastructure.github.service.dto.response.GithubIssueDetailResponse;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;
import soon.capstone.infrastructure.restclient.config.RestClientConfig;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubIssueService {

    private static final String ISSUE_URL = "/repos/{organizationName}/{repositoryName}/issues";

    private final RestClientConfig restClientConfig;
    private final OAuthTokenRepository oAuthTokenRepository;

    public Long createGithubIssue(GithubIssueCreateServiceRequest request) {
        try {
            String token = oAuthTokenRepository.findByMemberId(request.memberId()).getToken();
            RestClient restClient = restClientConfig.githubRestClient(token);

            String uri = ISSUE_URL
                .replace("{organizationName}", request.organizationName())
                .replace("{repositoryName}", request.repositoryName());

            GithubIssueCreateResponse response = restClient.post()
                .uri(uri)
                .body(request.toGithubRequest())
                .retrieve()
                .body(GithubIssueCreateResponse.class);

            return response.number();
        } catch (HttpClientErrorException e) {
            log.error("GitHub API 호출 중 오류 발생: {}", e.getMessage(), e);
            throw new GithubHttpClientException();
        } catch (Exception e) {
            log.error("issue 추가 중 에러 발생", e);
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
            String token = oAuthTokenRepository.findByMemberId(request.memberId()).getToken();
            RestClient restClient = restClientConfig.githubRestClient(token);

            String uri = (ISSUE_URL + "/{issueNumber}")
                .replace("{organizationName}", request.organizationName())
                .replace("{repositoryName}", request.repositoryName())
                .replace("{issueNumber}", String.valueOf(request.issueNumber()));

            restClient.patch()
                .uri(uri)
                .body(request.toGithubRequest())
                .retrieve()
                .body(Void.class);

        } catch (HttpClientErrorException e) {
            log.error("GitHub API 호출 중 오류 발생: {}", e.getMessage(), e);
            throw new GithubHttpClientException();
        } catch (Exception e) {
            log.error("issue 수정 중 에러 발생", e);
            throw new GithubHttpClientException();
        }
    }

    public IssueDetailResponse getIssueDetail(GithubIssueDetailServiceRequest request) {
        try {
            String token = oAuthTokenRepository.findByMemberId(request.memberId()).getToken();
            RestClient restClient = restClientConfig.githubRestClient(token);

            String uri = (ISSUE_URL + "/{issueNumber}")
                .replace("{organizationName}", request.organizationName())
                .replace("{repositoryName}", request.repositoryName())
                .replace("{issueNumber}", String.valueOf(request.issueNumber()));

            GithubIssueDetailResponse response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(GithubIssueDetailResponse.class);

            return response.toIssueDetailResponse();

        } catch (HttpClientErrorException e) {
            log.error("GitHub API 호출 중 오류 발생: {}", e.getMessage(), e);
            throw new GithubHttpClientException();
        } catch (Exception e) {
            log.error("issue 조회 중 에러 발생", e);
            throw new GithubHttpClientException();
        }
    }

}
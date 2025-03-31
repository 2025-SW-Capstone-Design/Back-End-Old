package soon.capstone.infrastructure.github.service.issue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import soon.capstone.infrastructure.github.service.dto.GithubIssueServiceRequest;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;
import soon.capstone.infrastructure.restclient.config.RestClientConfig;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubIssueService {

    private static final String ISSUE_URL = "/repos/{organizationName}/{repositoryName}/issues";

    private final RestClientConfig restClientConfig;
    private final OAuthTokenRepository oAuthTokenRepository;

    public void createGithubIssue(GithubIssueServiceRequest request) {
        try {
            String token = oAuthTokenRepository.findByMemberId(request.memberId()).getToken();
            RestClient restClient = restClientConfig.githubRestClient(token);

            String uri = ISSUE_URL
                .replace("{organizationName}", request.organizationName())
                .replace("{repositoryName}", request.repositoryName());

            restClient.post()
                .uri(uri)
                .body(request.toGithubRequest())
                .retrieve()
                .body(Void.class);

        } catch (HttpClientErrorException e) {
            log.error("GitHub API 호출 중 오류 발생: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("issue 추가 중 에러 발생", e);
        }
    }

}
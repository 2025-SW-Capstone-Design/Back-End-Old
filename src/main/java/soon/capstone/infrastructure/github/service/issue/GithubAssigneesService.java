package soon.capstone.infrastructure.github.service.issue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import soon.capstone.infrastructure.github.service.dto.GithubAssigneesServiceRequest;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;
import soon.capstone.infrastructure.restclient.config.RestClientConfig;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubAssigneesService {

    private static final String ASSIGN_URL = "/repos/{organizationName}/{repositoryName}/assignees/{assignee}";

    private final RestClientConfig restClientConfig;
    private final OAuthTokenRepository oAuthTokenRepository;

    public boolean isAssignee(GithubAssigneesServiceRequest request) {
        try {
            String token = oAuthTokenRepository.findByMemberId(request.memberId()).getToken();
            RestClient restClient = restClientConfig.githubRestClient(token);

            String uri = ASSIGN_URL
                .replace("{organizationName}", request.organizationName())
                .replace("{repositoryName}", request.repositoryName())
                .replace("{assignee}", request.assignee());

            restClient.get()
                .uri(uri)
                .retrieve()
                .body(Void.class);

            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // 추가할 수 있는 assignee가 아닌 경우 404 Error 발생
                return false;
            }

            log.error("GitHub API 호출 중 오류 발생: {}", e.getMessage(), e);
        }

        return false;
    }

}
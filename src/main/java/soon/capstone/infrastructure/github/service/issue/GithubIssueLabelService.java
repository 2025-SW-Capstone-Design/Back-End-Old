package soon.capstone.infrastructure.github.service.issue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import soon.capstone.global.exception.issue.label.AlreadyIssueLabelException;
import soon.capstone.infrastructure.github.service.dto.GithubIssueLabelServiceRequest;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;
import soon.capstone.infrastructure.restclient.config.RestClientConfig;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubIssueLabelService {

    private static final String ISSUE_LABEL_URL = "/repos/{organizationName}/{repositoryName}/labels";

    private final RestClientConfig restClientConfig;
    private final OAuthTokenRepository oAuthTokenRepository;

    public void createGithubIssueLabel(GithubIssueLabelServiceRequest request) {
        try {
            String token = oAuthTokenRepository.findByMemberId(request.memberId()).getToken();
            RestClient restClient = restClientConfig.githubRestClient(token);

            String uri = ISSUE_LABEL_URL
                .replace("{organizationName}", request.organizationName())
                .replace("{repositoryName}", request.repositoryName());

            restClient.post()
                .uri(uri)
                .body(request.toGithubRequest())
                .retrieve()
                .body(Void.class);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == UNPROCESSABLE_ENTITY) {
                log.info("라벨 '{}' 이미 GitHub에 존재합니다.", request.title());
                throw new AlreadyIssueLabelException();
            }

            log.error("GitHub API 호출 중 오류 발생: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("issue label 추가 중 에러 발생", e);
        }
    }

}
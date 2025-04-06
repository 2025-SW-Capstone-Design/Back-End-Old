package soon.capstone.infrastructure.github.service.issue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import soon.capstone.global.exception.github.GithubIssueLabelNotFoundException;
import soon.capstone.global.exception.issue.label.AlreadyIssueLabelException;
import soon.capstone.infrastructure.github.dto.GithubIssueLabelDetailDto;
import soon.capstone.infrastructure.github.service.dto.GithubIssueLabelCreateServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueLabelDeleteServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueLabelDetailServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueLabelUpdateServiceRequest;
import soon.capstone.infrastructure.redis.oauth2.repository.OAuthTokenRepository;
import soon.capstone.infrastructure.restclient.config.RestClientConfig;

import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubIssueLabelService {

    private static final String ISSUE_LABEL_URL = "/repos/{organizationName}/{repositoryName}/labels";

    private final RestClientConfig restClientConfig;
    private final OAuthTokenRepository oAuthTokenRepository;

    public void createGithubIssueLabel(GithubIssueLabelCreateServiceRequest request) {
        try {
            String token = oAuthTokenRepository.findByMemberId(request.memberId()).getToken();
            RestClient restClient = restClientConfig.githubRestClient(token);

            String uri = buildUri(request.organizationName(), request.repositoryName());

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

    public void updateGithubIssueLabel(GithubIssueLabelUpdateServiceRequest request) {
        try {
            String token = oAuthTokenRepository.findByMemberId(request.memberId()).getToken();
            RestClient restClient = restClientConfig.githubRestClient(token);

            String uri = buildUri(request.organizationName(), request.repositoryName());

            restClient.patch()
                .uri(uri + "/" + request.oldTitle())
                .body(request.toGithubRequest())
                .retrieve()
                .body(Void.class);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == NOT_FOUND) {
                log.info("라벨 '{}'이 GitHub에 존재하지 않습니다.", request.oldTitle());
                throw new GithubIssueLabelNotFoundException();
            }

            log.error("GitHub API 호출 중 오류 발생: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("issue label 수정 중 에러 발생", e);
        }
    }

    public void deleteGithubIssueLabel(GithubIssueLabelDeleteServiceRequest request) {
        try {
            String token = oAuthTokenRepository.findByMemberId(request.memberId()).getToken();
            RestClient restClient = restClientConfig.githubRestClient(token);

            String uri = buildUri(request.organizationName(), request.repositoryName());

            restClient.delete()
                .uri(uri + "/" + request.title())
                .retrieve()
                .body(Void.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == NOT_FOUND) {
                log.info("라벨 '{}'이 GitHub에 존재하지 않습니다.", request.title());
                throw new GithubIssueLabelNotFoundException();
            }

            log.error("GitHub API 호출 중 오류 발생: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("issue label 수정 중 에러 발생", e);
        }
    }

    public List<GithubIssueLabelDetailDto> getIssueLabels(GithubIssueLabelDetailServiceRequest request) {
        try {
            String token = oAuthTokenRepository.findByMemberId(request.memberId()).getToken();
            RestClient restClient = restClientConfig.githubRestClient(token);

            String uri = buildUri(request.organizationName(), request.repositoryName());
            return restClient.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GithubIssueLabelDetailDto>>() {
                });

        } catch (HttpClientErrorException e) {
            log.error("GitHub API 호출 중 오류 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("이슈 라벨 조회 중 예상치 못한 오류 발생", e);
            return Collections.emptyList();
        }
    }

    private String buildUri(String organizationName, String repositoryName) {
        return ISSUE_LABEL_URL
            .replace("{organizationName}", organizationName)
            .replace("{repositoryName}", repositoryName);
    }

}
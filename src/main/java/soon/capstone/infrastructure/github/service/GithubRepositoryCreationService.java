package soon.capstone.infrastructure.github.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import soon.capstone.global.exception.github.GithubHttpClientException;
import soon.capstone.infrastructure.github.dto.GithubRepositoryCreationDto;
import soon.capstone.infrastructure.restclient.config.RestClientConfig;

@RequiredArgsConstructor
@Service
public class GithubRepositoryCreationService {

    private static final String REPOSITORY_URL = "/orgs/{orgName}/repos";
    private final RestClientConfig restClientConfig;

    public void createRepositoryForOrganization(String oauthToken, String orgName, String repoName) {
        RestClient restClient = restClientConfig.githubRestClient(oauthToken);

        try {
            restClient.post()
                    .uri(REPOSITORY_URL.replace("{orgName}", orgName))
                    .body(GithubRepositoryCreationDto.of(repoName))
                    .retrieve()
                    .toBodilessEntity();

        } catch (HttpClientErrorException e) {
            throw new GithubHttpClientException();
        }
    }
}

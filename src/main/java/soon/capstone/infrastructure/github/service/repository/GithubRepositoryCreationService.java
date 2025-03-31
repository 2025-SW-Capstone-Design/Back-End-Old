package soon.capstone.infrastructure.github.service.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import soon.capstone.global.exception.github.GithubHttpClientException;
import soon.capstone.infrastructure.github.dto.GithubRepositoryCreationDto;
import soon.capstone.infrastructure.github.dto.GithubRepositoryResponseDto;
import soon.capstone.infrastructure.restclient.config.RestClientConfig;

@RequiredArgsConstructor
@Service
public class GithubRepositoryCreationService {

    private static final String REPOSITORY_URL = "/orgs/{orgName}/repos";
    private final RestClientConfig restClientConfig;

    public String createRepositoryForOrganization(String oauthToken, String orgName, String repoName) {
        RestClient restClient = restClientConfig.githubRestClient(oauthToken);

        try {
            ResponseEntity<GithubRepositoryResponseDto> response = restClient.post()
                    .uri(REPOSITORY_URL.replace("{orgName}", orgName))
                    .body(GithubRepositoryCreationDto.of(repoName))
                    .retrieve()
                    .toEntity(GithubRepositoryResponseDto.class);
            return response.getBody().nodeId();

        } catch (HttpClientErrorException e) {
            throw new GithubHttpClientException();
        }
    }
}

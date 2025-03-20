package soon.capstone.infrastructure.restclient.config;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static soon.capstone.global.security.jwt.common.TokenType.AUTHORIZATION_HEADER;
import static soon.capstone.global.security.jwt.common.TokenType.BEARER_PREFIX;

@Component
public class RestClientConfig {

    private static final String GITHUB_BASE_URL = "https://api.github.com";

    public RestClient githubRestClient(String oauth2Token) {
        return RestClient.builder()
            .baseUrl(GITHUB_BASE_URL)
            .defaultHeader(AUTHORIZATION_HEADER.getValue(), BEARER_PREFIX.getValue() + oauth2Token)
            .defaultHeader("Accept", "application/vnd.github+json")
            .build();
    }

    public RestClient openAiRestClient() {
        throw new UnsupportedOperationException("openAiRestClient is not implemented");
    }

}
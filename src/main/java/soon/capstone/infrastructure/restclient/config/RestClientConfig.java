package soon.capstone.infrastructure.restclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static soon.capstone.global.security.jwt.common.TokenType.AUTHORIZATION_HEADER;
import static soon.capstone.global.security.jwt.common.TokenType.BEARER_PREFIX;

@Component
public class RestClientConfig {

    @Value("${openai.api.key}")
    private String openAiKey;

    private static final String GITHUB_BASE_URL = "https://api.github.com";
    private static final String OPEN_AI_CHAT_BASE_URL = "https://api.openai.com/v1/chat/completions";
    private static final String OPEN_AI_BASE_URL = "https://api.openai.com/v1";

    public RestClient githubRestClient(String oauth2Token) {
        return RestClient.builder()
            .baseUrl(GITHUB_BASE_URL)
            .defaultHeader(AUTHORIZATION_HEADER.getValue(), BEARER_PREFIX.getValue() + oauth2Token)
            .defaultHeader("Accept", "application/vnd.github+json")
            .build();
    }

    public RestClient openAiRestClient() {
        return RestClient.builder()
            .baseUrl(OPEN_AI_BASE_URL)
            .defaultHeader(AUTHORIZATION_HEADER.getValue(), BEARER_PREFIX.getValue() + openAiKey)
            .defaultHeader("Content-Type", APPLICATION_JSON_VALUE)
            .build();
    }

    public RestClient openAiChatRestClient() {
        return RestClient.builder()
            .baseUrl(OPEN_AI_CHAT_BASE_URL)
            .defaultHeader(AUTHORIZATION_HEADER.getValue(), BEARER_PREFIX.getValue() + openAiKey)
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

}
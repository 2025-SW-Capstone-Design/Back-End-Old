package soon.capstone.infrastructure.graphQL;

import org.springframework.graphql.client.HttpSyncGraphQlClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

import static soon.capstone.global.security.jwt.common.TokenType.AUTHORIZATION_HEADER;
import static soon.capstone.global.security.jwt.common.TokenType.BEARER_PREFIX;

@Component
public class GraphQLClientConfig {

    private static final String GITHUB_GRAPHQL_BASE_URL = "https://api.github.com/graphql";
    private static final String GITHUB_MEDIA_TYPE = "application/json";

    public HttpSyncGraphQlClient queryClient(String oauth2Token) {
        return HttpSyncGraphQlClient.builder(createRestClient())
                .headers(headers -> {
                    headers.set(AUTHORIZATION_HEADER.getValue(), BEARER_PREFIX.getValue() + oauth2Token);
                    headers.setAccept(List.of(MediaType.valueOf(GITHUB_MEDIA_TYPE)));
                })
                .build();
    }

    public HttpSyncGraphQlClient mutationClient(String oauth2Token) {
        return queryClient(oauth2Token).mutate().build();
    }

    private RestClient createRestClient() {
        return RestClient.create(GITHUB_GRAPHQL_BASE_URL);
    }

}

package soon.capstone.infrastructure.github.service.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.global.exception.github.GithubHttpClientException;
import soon.capstone.infrastructure.github.service.constant.GithubQuery;
import soon.capstone.infrastructure.graphQL.GraphQLClientConfig;

@RequiredArgsConstructor
@Service
public class GithubOrganizationReadService {

    private final GraphQLClientConfig graphQLClientConfig;

    public String getOrganizationId(String organizationName, String oauthToken) {
        String formattedQuery = String.format(GithubQuery.GET_ORGANIZATION.getQuery(), organizationName);

        try {
            return graphQLClientConfig.queryClient(oauthToken)
                    .document(formattedQuery)
                    .retrieve("organization.id")
                    .toEntity(String.class)
                    .block();
        } catch (Exception e) {
            throw new GithubHttpClientException();
        }
    }

}

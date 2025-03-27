package soon.capstone.infrastructure.github.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.infrastructure.github.service.constant.GithubQuery;
import soon.capstone.infrastructure.graphQL.GraphQLClientConfig;

@RequiredArgsConstructor
@Service
public class GithubOrganizationReadService {

    private final GraphQLClientConfig graphQLClientConfig;

    public String getOrganizationId(String organizationName, String oauthToken) {

        String formattedQuery = String.format(GithubQuery.GET_ORGANIZATION.getQuery(), organizationName);

        return graphQLClientConfig.queryClient(oauthToken)
                .document(formattedQuery)
                .retrieve("organization.id")
                .toEntity(String.class)
                .block();
    }

}

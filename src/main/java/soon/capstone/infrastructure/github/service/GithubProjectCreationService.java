package soon.capstone.infrastructure.github.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.infrastructure.github.service.constant.GithubQuery;
import soon.capstone.infrastructure.graphQL.GraphQLClientConfig;

@RequiredArgsConstructor
@Service
public class GithubProjectCreationService {

    private final GraphQLClientConfig graphQLClientConfig;

    public String createProject(String organizationId, String title, String oauthToken) {
        String formattedMutation = String.format(GithubQuery.CREATE_PROJECT.getQuery(), organizationId, title);

        var response = graphQLClientConfig.mutationClient(oauthToken)
                .document(formattedMutation)
                .retrieve("createProjectV2.projectV2.id")
                .toEntity(String.class)
                .block();

        System.out.println("GitHub API Response (Project ID): " + response);
        return response;
    }
}

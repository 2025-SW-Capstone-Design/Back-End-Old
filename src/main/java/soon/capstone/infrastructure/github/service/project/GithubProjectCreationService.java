package soon.capstone.infrastructure.github.service.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.global.exception.github.GithubHttpClientException;
import soon.capstone.infrastructure.github.service.constant.GithubQuery;
import soon.capstone.infrastructure.graphQL.GraphQLClientConfig;

@RequiredArgsConstructor
@Service
public class GithubProjectCreationService {

    private final GraphQLClientConfig graphQLClientConfig;
    private static final String PROJECT_NAME = "%s-Project";

    public String createProject(String organizationId, String organizationName, String oauthToken) {
        String formattedMutation = String.format(GithubQuery.CREATE_PROJECT.getQuery(), organizationId, String.format(PROJECT_NAME, organizationName));

        try {
            return graphQLClientConfig.mutationClient(oauthToken)
                    .document(formattedMutation)
                    .retrieve("createProjectV2.projectV2.id")
                    .toEntity(String.class)
                    .block();
        } catch (Exception e) {
            throw new GithubHttpClientException();
        }
    }
}

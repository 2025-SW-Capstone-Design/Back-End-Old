package soon.capstone.infrastructure.github.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import soon.capstone.global.exception.github.GithubHttpClientException;
import soon.capstone.infrastructure.github.service.constant.GithubQuery;
import soon.capstone.infrastructure.graphQL.GraphQLClientConfig;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubLinkProjectToRepositoryService {

    private final GraphQLClientConfig graphQLClientConfig;

    public void linkProjectToRepository(String projectId, String repositoryId, String oauthToken) {
        String formattedQuery = String.format(GithubQuery.LINK_PROJECT_TO_REPOSITORY.getQuery(), projectId, repositoryId);

        try {
            graphQLClientConfig.queryClient(oauthToken)
                    .document(formattedQuery)
                    .retrieve("linkProjectV2ToRepository.repository.name")
                    .toEntity(String.class)
                    .block();
            log.info("Project linked to repository successfully: projectId={}, repositoryId={}", projectId, repositoryId);
        } catch (Exception e) {
            throw new GithubHttpClientException();
        }
    }
}

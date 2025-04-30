package soon.capstone.infrastructure.github.service.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.global.exception.github.GithubHttpClientException;
import soon.capstone.infrastructure.github.service.constant.KanbanStatus;
import soon.capstone.infrastructure.graphQL.GraphQLClientConfig;

import static soon.capstone.infrastructure.github.service.constant.GithubQuery.*;

@RequiredArgsConstructor
@Service
public class GithubProjectCreationService {

    private final GraphQLClientConfig graphQLClientConfig;
    private static final String PROJECT_NAME = "%s-Project";

    public String setupKanbanProject(String organizationId, String organizationName, String oauthToken) {

        String projectId = createProject(organizationId, organizationName, oauthToken);
        String statusFieldId = addStatusField(projectId, oauthToken);

        for (KanbanStatus status : KanbanStatus.values()) {
            addStatusOption(projectId, statusFieldId, status.getName(), status.getColor(), oauthToken);
        }

        String viewId = createBoardView(projectId, oauthToken);
        configureBoardView(projectId, viewId, statusFieldId, oauthToken);

        return projectId;
    }

    private String createProject(String organizationId, String organizationName, String oauthToken) {
        String formattedMutation = String.format(CREATE_PROJECT.getQuery(),
            organizationId,
            String.format(PROJECT_NAME, organizationName));

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

    private String addStatusField(String projectId, String oauthToken) {
        String mutation = String.format(ADD_STATUS_FIELD.getQuery(), projectId);

        try {
            return graphQLClientConfig.mutationClient(oauthToken)
                .document(mutation)
                .retrieve("addProjectV2Field.projectV2Field.id")
                .toEntity(String.class)
                .block();
        } catch (Exception e) {
            throw new GithubHttpClientException();
        }
    }

    private void addStatusOption(String projectId, String fieldId, String optionName, String optionColor, String oauthToken) {
        String mutation = String.format(ADD_STATUS_FIELD_OPTION.getQuery(),
            projectId, fieldId, optionName, optionColor);

        try {
            graphQLClientConfig.mutationClient(oauthToken)
                .document(mutation)
                .retrieve("createProjectV2FieldOption.projectV2FieldOption.id")
                .toEntity(String.class)
                .block();
        } catch (Exception e) {
            throw new GithubHttpClientException();
        }
    }

    private String createBoardView(String projectId, String oauthToken) {
        String mutation = String.format(CREATE_BOARD_VIEW.getQuery(), projectId);

        try {
            return graphQLClientConfig.mutationClient(oauthToken)
                .document(mutation)
                .retrieve("createProjectV2View.projectV2View.id")
                .toEntity(String.class)
                .block();
        } catch (Exception e) {
            throw new GithubHttpClientException();
        }
    }

    private void configureBoardView(String projectId, String viewId, String fieldId, String oauthToken) {
        String mutation = String.format(CONFIGURE_BOARD_VIEW.getQuery(),
            projectId, viewId, fieldId);

        try {
            graphQLClientConfig.mutationClient(oauthToken)
                .document(mutation)
                .retrieve("updateProjectV2View.projectV2View.id")
                .toEntity(String.class)
                .block();
        } catch (Exception e) {
            throw new GithubHttpClientException();
        }
    }
}

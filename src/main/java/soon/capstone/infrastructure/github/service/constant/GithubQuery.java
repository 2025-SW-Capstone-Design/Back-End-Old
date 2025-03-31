package soon.capstone.infrastructure.github.service.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GithubQuery {

    GET_ORGANIZATION("""
        query {
            organization(login: "%s") {
                id
            }
        }
    """),
    CREATE_PROJECT("""
        mutation {
            createProjectV2(input: {ownerId: "%s", title: "%s"}) {
                projectV2 {
                    id
                    title
                }
            }
        }
    """),
    LINK_PROJECT_TO_REPOSITORY(
        """
        mutation {
            linkProjectV2ToRepository(input: {projectId: "%s", repositoryId: "%s"}) {
                repository {
                    name
                }
            }
        }
    """);

    private final String query;
}

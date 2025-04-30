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
    ADD_STATUS_FIELD("""
        mutation {
            addProjectV2Field(input: {
                projectId: "%s",
                dataType: SINGLE_SELECT,
                name: "Status"
            }) {
                projectV2Field {
                    id
                    name
                }
            }
        }
    """),
    ADD_STATUS_FIELD_OPTION("""
        mutation {
            createProjectV2FieldOption(input: {
                projectId: "%s",
                fieldId: "%s",
                name: "%s",
                color: %s
            }) {
                projectV2FieldOption {
                    id
                    name
                }
            }
        }
    """),
    CREATE_BOARD_VIEW("""
        mutation {
            createProjectV2View(input: {
                projectId: "%s",
                name: "Kanban Board",
                layout: BOARD_LAYOUT
            }) {
                projectV2View {
                    id
                    name
                    layout
                }
            }
        }
    """),
    CONFIGURE_BOARD_VIEW("""
        mutation {
            updateProjectV2View(input: {
                projectId: "%s",
                viewId: "%s",
                fieldId: "%s"
            }) {
                projectV2View {
                    id
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

package soon.capstone.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.infrastructure.github.service.project.GithubLinkProjectToRepositoryService;
import soon.capstone.infrastructure.github.service.organization.GithubOrganizationReadService;
import soon.capstone.infrastructure.github.service.project.GithubProjectCreationService;

@RequiredArgsConstructor
@Service
public class OrganizationProjectCreationService {

    private final GithubOrganizationReadService githubOrganizationReadService;
    private final GithubProjectCreationService githubProjectCreationService;
    private final GithubLinkProjectToRepositoryService githubLinkProjectToRepositoryService;

    public void createProject(String organizationName, String oauthToken, String repositoryId, String repoName) {
        String organizationId = getOrganizationId(organizationName, oauthToken);
        String projectId = githubProjectCreationService.setupKanbanProject(organizationId, repoName, oauthToken);
        linkProjectToRepository(projectId, repositoryId, oauthToken);
    }

    private String getOrganizationId(String organizationName, String oauthToken) {
        return githubOrganizationReadService.getOrganizationId(organizationName, oauthToken);
    }

    private void linkProjectToRepository(String projectId, String repositoryId, String oauthToken) {
        githubLinkProjectToRepositoryService.linkProjectToRepository(projectId, repositoryId, oauthToken);
    }
}

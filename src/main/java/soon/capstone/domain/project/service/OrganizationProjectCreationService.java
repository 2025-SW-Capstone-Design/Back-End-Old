package soon.capstone.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.infrastructure.github.service.GithubOrganizationReadService;
import soon.capstone.infrastructure.github.service.GithubProjectCreationService;

@RequiredArgsConstructor
@Service
public class OrganizationProjectCreationService {

    private final GithubOrganizationReadService githubOrganizationReadService;
    private final GithubProjectCreationService githubProjectCreationService;

    public void createProject(String organizationName, String oauthToken) {
        String organizationId = getOrganizationId(organizationName, oauthToken);
        githubProjectCreationService.createProject(organizationId, organizationName, oauthToken);
    }

    private String getOrganizationId(String organizationName, String oauthToken) {
        return githubOrganizationReadService.getOrganizationId(organizationName, oauthToken);
    }
}

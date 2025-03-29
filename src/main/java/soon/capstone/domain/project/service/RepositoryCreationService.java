package soon.capstone.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.project.repository.ProjectRepository;
import soon.capstone.domain.project.service.dto.response.RepositoryCreationEvent;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.infrastructure.github.service.GithubRepositoryCreationService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RepositoryCreationService {

    private static final List<String> REPOSITORY_TEMPLATES = List.of("%s-Frontend", "%s-Backend");

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final GithubRepositoryCreationService githubRepositoryCreationService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void createRepository(Long teamId, String creator, String oauthToken) {
        Team team = teamRepository.findById(teamId);
        String organizationName = team.getOrganizationName();

        REPOSITORY_TEMPLATES.forEach(template ->
                createRepositoryAndProject(String.format(template, organizationName), organizationName, creator, oauthToken, team)
        );

        applicationEventPublisher.publishEvent(
                RepositoryCreationEvent.builder()
                        .oauthToken(oauthToken)
                        .organizationName(organizationName)
                        .build()
        );
    }

    private void createRepositoryAndProject(String repoName, String organizationName, String creator, String oauthToken, Team team) {
        githubRepositoryCreationService.createRepositoryForOrganization(oauthToken, organizationName, repoName);
        projectRepository.save(Project.builder()
                .creator(creator)
                .title(repoName)
                .team(team)
                .build());
    }

}
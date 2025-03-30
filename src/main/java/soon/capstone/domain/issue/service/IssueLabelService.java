package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.issue.repository.issuelabel.IssueLabelRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.global.exception.issue.label.AlreadyIssueLabelException;
import soon.capstone.infrastructure.github.service.GithubIssueLabelService;
import soon.capstone.infrastructure.github.service.dto.GithubIssueLabelServiceRequest;

@RequiredArgsConstructor
@Service
public class IssueLabelService {

    private final IssueLabelRepository issueLabelRepository;
    private final GithubIssueLabelService githubIssueLabelService;

    public Long createIssueLabel(
        String title,
        String description,
        String color,
        Project project,
        Long memberId,
        Team team
    ) {
        validateLabelNotExists(title, project);

        createGithubIssueLabel(title, description, color, project, memberId, team);

        IssueLabel issueLabel = IssueLabel.createIssueLabel(
            color, title, description, team, project
        );
        return issueLabelRepository.save(issueLabel)
            .getId();
    }

    private void validateLabelNotExists(String title, Project project) {
        boolean alreadyExists = issueLabelRepository.existsByTitleAndProject(title, project);
        if (alreadyExists) {
            throw new AlreadyIssueLabelException();
        }
    }

    private void createGithubIssueLabel(
        String title,
        String description,
        String color,
        Project project,
        Long memberId,
        Team team
    ) {
        GithubIssueLabelServiceRequest request = GithubIssueLabelServiceRequest.builder()
            .memberId(memberId)
            .title(title)
            .description(description)
            .color(color)
            .organizationName(team.getOrganizationName())
            .repositoryName(project.getTitle())
            .build();

        githubIssueLabelService.createGithubIssueLabel(request);
    }

}
package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.issue.repository.issuelabel.IssueLabelRepository;
import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.global.exception.issue.label.AlreadyIssueLabelException;
import soon.capstone.infrastructure.github.service.dto.GithubIssueLabelCreateServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueLabelDeleteServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueLabelDetailServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubIssueLabelUpdateServiceRequest;
import soon.capstone.infrastructure.github.service.issue.GithubIssueLabelService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class IssueLabelService {

    private final IssueLabelRepository issueLabelRepository;
    private final GithubIssueLabelService githubIssueLabelService;

    // repositoryName == project.title
    @CacheEvict(value = "issueLabels", key = "#team.organizationName + '_' + #project.title")
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

    @CacheEvict(value = "issueLabels", key = "#organizationName + '_' + #project.title")
    @Transactional
    public void updateIssueLabel(
        Long labelId,
        String oldTitle,
        String newTitle,
        String description,
        String color,
        String organizationName,
        String repositoryName,
        Project project,
        Long memberId
    ) {
        validateLabelNotExists(newTitle, project);

        updateGithubIssueLabel(
            oldTitle, newTitle, description, color, organizationName, repositoryName, memberId
        );

        IssueLabel issueLabel = issueLabelRepository.findById(labelId);
        issueLabel.update(newTitle, description, color);
    }

    @CacheEvict(value = "issueLabels", key = "#organizationName + '_' + #repositoryName")
    @Transactional
    public void deleteIssueLabel(
        Long memberId,
        Long labelId,
        String organizationName,
        String repositoryName,
        String title
    ) {
        deleteGithubIssueLabel(memberId, organizationName, repositoryName, title);

        issueLabelRepository.deleteById(labelId);
    }

    @Cacheable(value = "issueLabels", key = "#team.organizationName + '_' + #project.title")
    public List<IssueLabelDetailResponse> getIssueLabels(
        Long memberId,
        Team team,
        Project project
    ) {
        List<IssueLabelDetailResponse> labelResponse = getGithubIssueLabels(
            memberId, team.getOrganizationName(), project.getTitle()
        );

        Map<String, IssueLabel> labelMap = issueLabelRepository.findAllByProject(project)
            .stream()
            .collect(Collectors.toMap(IssueLabel::getTitle, Function.identity()));

        return labelResponse.stream()
            .map(response -> {
                IssueLabel label = labelMap.get(response.getName());
                return response.withLabelId(label.getId());
            })
            .collect(Collectors.toList());
    }

    @Cacheable(value = "issueLabels", key = "#team.organizationName + '_' + #project.title")
    @Transactional
    public void initializeIssueLabels(
        Long memberId,
        Project project,
        Team team
    ) {
        log.info("이슈 라벨 초기화: {}", project.getTitle());
        List<IssueLabel> labels = getGithubIssueLabels(memberId, team.getOrganizationName(), project.getTitle()).stream()
            .map(label -> IssueLabel.createIssueLabel(label.getColor(), label.getName(), label.getDescription(), team, project))
            .toList();
        log.info("초기화 할 라벨 개수: {}", labels.size());

        issueLabelRepository.saveAll(labels);

        List<IssueLabel> allByProject = issueLabelRepository.findAllByProject(project);
        log.info("DB에 저장된 라벨 개수: {}", allByProject.size());
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
        GithubIssueLabelCreateServiceRequest request = GithubIssueLabelCreateServiceRequest.builder()
            .memberId(memberId)
            .title(title)
            .description(description)
            .color(color)
            .organizationName(team.getOrganizationName())
            .repositoryName(project.getTitle())
            .build();

        githubIssueLabelService.createGithubIssueLabel(request);
    }

    private void updateGithubIssueLabel(
        String oldTitle,
        String newTitle,
        String description,
        String color,
        String organizationName,
        String repositoryName,
        Long memberId
    ) {
        GithubIssueLabelUpdateServiceRequest request = GithubIssueLabelUpdateServiceRequest.builder()
            .oldTitle(oldTitle)
            .newTitle(newTitle)
            .description(description)
            .color(color)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .memberId(memberId)
            .build();
        githubIssueLabelService.updateGithubIssueLabel(request);
    }

    private void deleteGithubIssueLabel(Long memberId, String organizationName, String repositoryName, String title) {
        GithubIssueLabelDeleteServiceRequest request = GithubIssueLabelDeleteServiceRequest.builder()
            .memberId(memberId)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .title(title)
            .build();
        githubIssueLabelService.deleteGithubIssueLabel(request);
    }

    private List<IssueLabelDetailResponse> getGithubIssueLabels(
        Long memberId,
        String organizationName,
        String repositoryName
    ) {
        GithubIssueLabelDetailServiceRequest request = GithubIssueLabelDetailServiceRequest.builder()
            .memberId(memberId)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .build();

        return githubIssueLabelService.getIssueLabels(request);
    }

}
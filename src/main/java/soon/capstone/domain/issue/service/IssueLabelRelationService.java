package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.issue.entity.Issue;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.issue.entity.IssueLabelRelation;
import soon.capstone.domain.issue.repository.issueLabelRelation.IssueLabelRelationRepository;
import soon.capstone.domain.issue.repository.issuelabel.IssueLabelRepository;
import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.infrastructure.github.service.dto.GithubIssueLabelAppendServiceRequest;
import soon.capstone.infrastructure.github.service.issue.GithubIssueLabelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class IssueLabelRelationService {

    private final GithubIssueLabelService githubIssueLabelService;
    private final IssueLabelRelationRepository issueLabelRelationRepository;
    private final IssueLabelRepository issueLabelRepository;

    @Transactional
    public void linkIssueWithLabels(
        Issue issue,
        List<String> labels,
        Long memberId,
        String organizationName,
        String repositoryName,
        Project project
    ) {
        appendLabelsToGithub(
            issue,
            labels,
            memberId,
            organizationName,
            repositoryName
        );

        List<IssueLabelRelation> relations = labels.stream()
            .map(label -> {
                IssueLabel issueLabel = issueLabelRepository.findByTitle(label, project);
                return IssueLabelRelation.createMapping(issue, issueLabel);
            })
            .toList();
        issueLabelRelationRepository.saveAll(relations);
    }

    @Transactional
    public void updateIssueRelation(Issue issue, List<String> labels) {
        List<IssueLabelRelation> existingRelations = issueLabelRelationRepository.findAllByIssue(issue);
        Map<String, IssueLabel> issueLabelMap = getIssueLabelMap(labels);

        List<IssueLabelRelation> relationsToRemove = getRelationsToRemove(existingRelations, issueLabelMap);
        List<IssueLabelRelation> relationsToAdd = getRelationsToAdd(issue, issueLabelMap);

        if (!relationsToRemove.isEmpty()) {
            issueLabelRelationRepository.deleteAllInBatch(relationsToRemove);
        }
        if (!relationsToAdd.isEmpty()) {
            issueLabelRelationRepository.saveAll(relationsToAdd);
        }
    }

    public List<IssueLabelDetailResponse> findByLabelsByIssueId(Issue issue) {
        List<IssueLabelRelation> relations = issueLabelRelationRepository.findAllByIssue(issue);
        return relations.stream()
            .map(relation -> IssueLabelDetailResponse.of(relation.getIssueLabel()))
            .collect(Collectors.toList());
    }

    private void appendLabelsToGithub(Issue issue, List<String> labels, Long memberId, String organizationName, String repositoryName) {
        GithubIssueLabelAppendServiceRequest githubRequest = GithubIssueLabelAppendServiceRequest.builder()
            .labels(labels)
            .issueNumber(issue.getGithubIssueNumber())
            .memberId(memberId)
            .repositoryName(repositoryName)
            .organizationName(organizationName)
            .build();
        githubIssueLabelService.appendLabelToIssue(githubRequest);
    }

    private Map<String, IssueLabel> getIssueLabelMap(List<String> labels) {
        List<IssueLabel> issueLabels = issueLabelRepository.findAllByTitleIn(labels);
        return issueLabels.stream()
            .collect(Collectors
                .toMap(IssueLabel::getTitle, label -> label)
            );
    }

    private List<IssueLabelRelation> getRelationsToRemove(
        List<IssueLabelRelation> existingRelations,
        Map<String, IssueLabel> issueLabelMap
    ) {
        List<IssueLabelRelation> relationsToRemove = new ArrayList<>();

        for (IssueLabelRelation relation : existingRelations) {
            String currentLabel = relation.getIssueLabel().getTitle();
            if (!issueLabelMap.containsKey(currentLabel)) {
                relationsToRemove.add(relation);
            }
        }

        return relationsToRemove;
    }

    private List<IssueLabelRelation> getRelationsToAdd(
        Issue issue,
        Map<String, IssueLabel> issueLabelMap
    ) {
        return issueLabelMap.values().stream()
            .map(label -> IssueLabelRelation.createMapping(issue, label))
            .toList();
    }

}
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
import soon.capstone.infrastructure.github.service.dto.GithubIssueLabelAppendServiceRequest;
import soon.capstone.infrastructure.github.service.issue.GithubIssueLabelService;

import java.util.List;

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
        String repositoryName
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
                IssueLabel issueLabel = issueLabelRepository.findByTitle(label);
                return IssueLabelRelation.createMapping(issue, issueLabel);
            })
            .toList();
        issueLabelRelationRepository.saveAll(relations);
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

}
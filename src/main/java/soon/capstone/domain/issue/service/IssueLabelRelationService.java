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

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class IssueLabelRelationService {

    private final IssueLabelRelationRepository issueLabelRelationRepository;
    private final IssueLabelRepository issueLabelRepository;

    @Transactional
    public void linkIssueWithLabels(Issue issue, List<String> labels) {
        List<IssueLabelRelation> relations = labels.stream()
            .map(label -> {
                IssueLabel issueLabel = issueLabelRepository.findByTitle(label);
                return IssueLabelRelation.createMapping(issue, issueLabel);
            })
            .toList();
        issueLabelRelationRepository.saveAll(relations);
    }

}
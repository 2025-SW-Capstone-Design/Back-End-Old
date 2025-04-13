package soon.capstone.domain.issue.repository.issueLabelRelation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.issue.entity.Issue;
import soon.capstone.domain.issue.entity.IssueLabelRelation;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class IssueLabelRelationRepository {

    private final IssueLabelRelationJpaRepository issueLabelRelationJpaRepository;

    public void save(IssueLabelRelation relation) {
        issueLabelRelationJpaRepository.save(relation);
    }

    public void saveAll(List<IssueLabelRelation> relations) {
        issueLabelRelationJpaRepository.saveAll(relations);
    }

    public void deleteAllInBatch() {
        issueLabelRelationJpaRepository.deleteAllInBatch();
    }

    public void deleteAllInBatch(List<IssueLabelRelation> relations) {
        issueLabelRelationJpaRepository.deleteAllInBatch(relations);
    }

    public List<IssueLabelRelation> findAllByIssue(Issue issue) {
        return issueLabelRelationJpaRepository.findAllByIssue(issue);
    }

}
package soon.capstone.domain.issue.repository.issueLabelRelation;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.issue.entity.Issue;
import soon.capstone.domain.issue.entity.IssueLabelRelation;

import java.util.List;

public interface IssueLabelRelationJpaRepository extends JpaRepository<IssueLabelRelation, Long>, IssueLabelRelationListRepository {

    List<IssueLabelRelation> findAllByIssue(Issue issue);

}
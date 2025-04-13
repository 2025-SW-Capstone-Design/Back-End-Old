package soon.capstone.domain.issue.repository.issueLabelRelation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import soon.capstone.domain.issue.entity.Issue;
import soon.capstone.domain.issue.entity.IssueLabelRelation;

import java.util.List;

public interface IssueLabelRelationJpaRepository extends JpaRepository<IssueLabelRelation, Long>, IssueLabelRelationListRepository {

    @Query("SELECT r FROM IssueLabelRelation r JOIN FETCH r.issueLabel WHERE r.issue = :issue")
    List<IssueLabelRelation> findAllByIssue(@Param("issue") Issue issue);

}
package soon.capstone.domain.issue.repository.issuelabel;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.issue.entity.IssueLabelRelation;

public interface IssueLabelRelationJpaRepository extends JpaRepository<IssueLabelRelation, Long> {
}
package soon.capstone.domain.issue.repository.issuelabel;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.issue.entity.IssueLabel;

public interface IssueLabelJpaRepository extends JpaRepository<IssueLabel, Long> {

}
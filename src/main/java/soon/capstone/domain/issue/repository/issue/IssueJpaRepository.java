package soon.capstone.domain.issue.repository.issue;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.issue.entity.Issue;

public interface IssueJpaRepository extends JpaRepository<Issue, Long> {

}
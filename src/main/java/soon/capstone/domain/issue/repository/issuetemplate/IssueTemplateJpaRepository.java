package soon.capstone.domain.issue.repository.issuetemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.issue.entity.IssueTemplate;

public interface IssueTemplateJpaRepository extends JpaRepository<IssueTemplate, Long> {

}
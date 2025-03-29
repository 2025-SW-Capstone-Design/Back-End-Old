package soon.capstone.domain.issue.repository.issuetemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.issue.entity.IssueTemplate;
import soon.capstone.domain.project.entity.Project;

public interface IssueTemplateJpaRepository extends JpaRepository<IssueTemplate, Long> {

    boolean existsByTitleAndProject(String title, Project project);

}
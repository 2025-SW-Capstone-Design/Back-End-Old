package soon.capstone.domain.issue.repository.issuelabel;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.project.entity.Project;

import java.util.List;

public interface IssueLabelJpaRepository extends JpaRepository<IssueLabel, Long> {

    boolean existsByTitleAndProject(String title, Project project);

    List<IssueLabel> findAllByProject(Project project);

}
package soon.capstone.domain.issue.repository.issuelabel;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.project.entity.Project;

import java.util.List;
import java.util.Optional;

public interface IssueLabelJpaRepository extends JpaRepository<IssueLabel, Long>, IssueLabelListRepository {

    boolean existsByTitleAndProject(String title, Project project);

    List<IssueLabel> findAllByProject(Project project);

    Optional<IssueLabel> findByTitleAndProject(String title, Project project);

    List<IssueLabel> findAllByTitleIn(List<String> titles);

}
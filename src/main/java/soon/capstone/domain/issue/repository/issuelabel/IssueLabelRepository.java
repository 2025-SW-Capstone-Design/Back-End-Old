package soon.capstone.domain.issue.repository.issuelabel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.global.exception.issue.label.IssueLabelNotFoundException;

@RequiredArgsConstructor
@Repository
public class IssueLabelRepository {

    private final IssueLabelJpaRepository issueLabelJpaRepository;

    public IssueLabel save(IssueLabel issueLabel) {
        return issueLabelJpaRepository.save(issueLabel);
    }

    public IssueLabel findById(Long id) {
        return issueLabelJpaRepository.findById(id)
            .orElseThrow(IssueLabelNotFoundException::new);
    }

    public boolean existsByTitleAndProject(String title, Project project) {
        return issueLabelJpaRepository.existsByTitleAndProject(title, project);
    }

    public void deleteAllInBatch() {
        issueLabelJpaRepository.deleteAllInBatch();
    }

}
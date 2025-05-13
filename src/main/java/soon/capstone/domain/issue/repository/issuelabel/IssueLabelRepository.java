package soon.capstone.domain.issue.repository.issuelabel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.global.exception.issue.label.IssueLabelNotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class IssueLabelRepository {

    private final IssueLabelJpaRepository issueLabelJpaRepository;

    public IssueLabel save(IssueLabel issueLabel) {
        return issueLabelJpaRepository.save(issueLabel);
    }

    public void saveAll(List<IssueLabel> issueLabels) {
        issueLabelJpaRepository.saveAll(issueLabels);
    }

    public IssueLabel findById(Long id) {
        return issueLabelJpaRepository.findById(id)
            .orElseThrow(IssueLabelNotFoundException::new);
    }

    public List<IssueLabel> findAllByProject(Project project) {
        return issueLabelJpaRepository.findAllByProject(project);
    }

    public IssueLabel findByTitle(String title, Project project) {
        return issueLabelJpaRepository.findByTitleAndProject(title, project)
            .orElseThrow(IssueLabelNotFoundException::new);
    }

    public List<IssueLabel> findAllByTitleInAndProject(List<String> titles, Project project) {
        return issueLabelJpaRepository.findAllByTitleInAndProject(titles, project);
    }

    public boolean existsByTitleAndProject(String title, Project project) {
        return issueLabelJpaRepository.existsByTitleAndProject(title, project);
    }

    public void deleteById(Long labelId) {
        issueLabelJpaRepository.deleteById(labelId);
    }

    public void deleteAllInBatch() {
        issueLabelJpaRepository.deleteAllInBatch();
    }

}
package soon.capstone.domain.issue.repository.issuetemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.issue.entity.IssueTemplate;
import soon.capstone.global.exception.issue.template.IssueTemplateNotFoundException;

@RequiredArgsConstructor
@Repository
public class IssueTemplateRepository {

    private final IssueTemplateJpaRepository issueTemplateJpaRepository;

    public IssueTemplate save(IssueTemplate issueTemplate) {
        return issueTemplateJpaRepository.save(issueTemplate);
    }

    public IssueTemplate findById(Long id) {
        return issueTemplateJpaRepository.findById(id)
            .orElseThrow(IssueTemplateNotFoundException::new);
    }

    public void deleteAllInBatch() {
        issueTemplateJpaRepository.deleteAllInBatch();
    }

}
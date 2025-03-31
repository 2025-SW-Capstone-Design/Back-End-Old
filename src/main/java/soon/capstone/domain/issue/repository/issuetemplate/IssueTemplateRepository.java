package soon.capstone.domain.issue.repository.issuetemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.issue.entity.IssueTemplate;
import soon.capstone.domain.issue.service.dto.response.IssueTemplateDetailResponse;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.global.exception.issue.template.IssueTemplateNotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class IssueTemplateRepository {

    private final IssueTemplateJpaRepository issueTemplateJpaRepository;

    public IssueTemplate save(IssueTemplate issueTemplate) {
        return issueTemplateJpaRepository.save(issueTemplate);
    }

    public void saveAll(List<IssueTemplate> template) {
        issueTemplateJpaRepository.saveAll(template);
    }

    public IssueTemplate findById(Long id) {
        return issueTemplateJpaRepository.findById(id)
            .orElseThrow(IssueTemplateNotFoundException::new);
    }

    public List<IssueTemplateDetailResponse> getIssueTemplates(String type, Project project) {
        return issueTemplateJpaRepository.getIssueTemplates(type, project);
    }

    public boolean existsByTitleAndProject(String title, Project project) {
        return issueTemplateJpaRepository.existsByTitleAndProject(title, project);
    }

    public void deleteById(Long id) {
        issueTemplateJpaRepository.deleteById(id);
    }

    public void deleteAllInBatch() {
        issueTemplateJpaRepository.deleteAllInBatch();
    }

}
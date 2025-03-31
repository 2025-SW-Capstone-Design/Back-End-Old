package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.issue.entity.IssueTemplate;
import soon.capstone.domain.issue.entity.IssueType;
import soon.capstone.domain.issue.repository.issuetemplate.IssueTemplateRepository;
import soon.capstone.domain.issue.service.dto.response.IssueTemplateDetailResponse;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.global.exception.issue.template.AlreadyIssueTemplateException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class IssueTemplateService {

    private final IssueTemplateRepository issueTemplateRepository;

    public Long createIssueTemplate(
        String title,
        String description,
        String content,
        String type,
        Project project
    ) {
        validateTemplateUniqueness(title, project);

        IssueTemplate issueTemplate = IssueTemplate.builder()
            .title(title)
            .description(description)
            .content(content)
            .type(IssueType.contains(type))
            .project(project)
            .build();

        return issueTemplateRepository.save(issueTemplate)
            .getId();
    }

    @Transactional
    public void updateIssueTemplate(
        Long issueTemplateId,
        String title,
        String description,
        String content,
        String type,
        Project project
    ) {
        validateTemplateUniqueness(title, project);

        IssueTemplate template = issueTemplateRepository.findById(issueTemplateId);
        template.update(title, description, content, IssueType.contains(type));
    }

    public IssueTemplateDetailResponse getIssueTemplate(Long issueTemplateId) {
        IssueTemplate issueTemplate = issueTemplateRepository.findById(issueTemplateId);
        return IssueTemplateDetailResponse.of(issueTemplate);
    }

    public List<IssueTemplateDetailResponse> getIssueTemplates(String type, Project project) {
        return issueTemplateRepository.getIssueTemplates(type, project);
    }

    @Transactional
    public void deleteIssueTemplate(Long issueTemplateId) {
        issueTemplateRepository.deleteById(issueTemplateId);
    }

    private void validateTemplateUniqueness(String title, Project project) {
        boolean alreadyExists = issueTemplateRepository.existsByTitleAndProject(title, project);
        if (alreadyExists) {
            throw new AlreadyIssueTemplateException();
        }
    }

}
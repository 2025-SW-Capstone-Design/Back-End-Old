package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.issue.entity.IssueTemplate;
import soon.capstone.domain.issue.entity.IssueType;
import soon.capstone.domain.issue.repository.issuetemplate.IssueTemplateRepository;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.global.exception.issue.template.AlreadyIssueTemplateException;

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
        boolean alreadyExists = issueTemplateRepository.existsByTitleAndProject(title, project);
        if (alreadyExists) {
            throw new AlreadyIssueTemplateException();
        }

        IssueType issueType = IssueType.contains(type);
        IssueTemplate issueTemplate = IssueTemplate.builder()
            .title(title)
            .description(description)
            .content(content)
            .type(issueType)
            .project(project)
            .build();

        return issueTemplateRepository.save(issueTemplate)
            .getId();
    }

}
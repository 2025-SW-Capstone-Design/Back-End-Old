package soon.capstone.domain.issue.repository.issuetemplate;

import soon.capstone.domain.issue.service.dto.response.IssueTemplateDetailResponse;
import soon.capstone.domain.project.entity.Project;

import java.util.List;

public interface IssueTemplateListRepository {

    List<IssueTemplateDetailResponse> getIssueTemplates(String type, Project project);

}
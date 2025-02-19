package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.issue.repository.issuetemplate.IssueTemplateRepository;

@RequiredArgsConstructor
@Service
public class IssueTemplateService {

    private final IssueTemplateRepository issueTemplateRepository;

}
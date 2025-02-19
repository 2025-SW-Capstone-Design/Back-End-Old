package soon.capstone.domain.issue.repository.issuetemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class IssueTemplateRepository {

    private final IssueTemplateJpaRepository issueTemplateJpaRepository;

}
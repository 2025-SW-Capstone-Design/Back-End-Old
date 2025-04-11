package soon.capstone.domain.issue.repository.issue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.issue.entity.Issue;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class IssueRepository {

    private final IssueJpaRepository issueJpaRepository;

    public void save(Issue issue) {
        issueJpaRepository.save(issue);
    }

    public void saveAll(List<Issue> issues) {
        issueJpaRepository.saveAll(issues);
    }

    public void deleteAllInBatch() {
        issueJpaRepository.deleteAllInBatch();
    }

    public List<IssueDetailResponse> findIssuesWithLabelsByMilestoneId(Long milestoneId) {
        return issueJpaRepository.findIssuesWithLabelsByMilestoneId(milestoneId);
    }
}
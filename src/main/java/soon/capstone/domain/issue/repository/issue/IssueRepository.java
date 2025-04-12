package soon.capstone.domain.issue.repository.issue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.issue.entity.Issue;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;
import soon.capstone.global.exception.issue.issue.IssueNotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class IssueRepository {

    private final IssueJpaRepository issueJpaRepository;

    public Long save(Issue issue) {
        return issueJpaRepository.save(issue)
            .getId();
    }

    public void saveAll(List<Issue> issues) {
        issueJpaRepository.saveAll(issues);
    }

    public Issue findById(Long id) {
        return issueJpaRepository.findById(id)
            .orElseThrow(IssueNotFoundException::new);
    }

    public void deleteAllInBatch() {
        issueJpaRepository.deleteAllInBatch();
    }

    public List<IssueDetailResponse> findIssuesWithLabelsByMilestoneId(Long milestoneId) {
        return issueJpaRepository.findIssuesWithLabelsByMilestoneId(milestoneId);
    }

}
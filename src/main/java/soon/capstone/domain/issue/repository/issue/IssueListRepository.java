package soon.capstone.domain.issue.repository.issue;

import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;

import java.util.List;

public interface IssueListRepository {
    List<IssueDetailResponse> findIssuesWithLabelsByMilestoneId(Long milestoneId);
}

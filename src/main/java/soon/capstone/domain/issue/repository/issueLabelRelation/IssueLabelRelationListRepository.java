package soon.capstone.domain.issue.repository.issueLabelRelation;

import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;

import java.util.List;

public interface IssueLabelRelationListRepository {
    List<IssueLabelDetailResponse> getIssueLabelsByIssueId(Long issueId);
}

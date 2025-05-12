package soon.capstone.domain.issue.service.dto.response;

import lombok.Builder;

@Builder
public record IssueDetailWrapperResponse(

    IssueDetailResponse issueDetail,
    Long teamMemberId

) {
}
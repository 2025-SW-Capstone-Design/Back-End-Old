package soon.capstone.domain.issue.service.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record IssueDetailResponse(

    Long issueId,
    String title,
    String content,
    String creator,
    String status,
    List<IssueLabelDetailResponse> labels

) {
}
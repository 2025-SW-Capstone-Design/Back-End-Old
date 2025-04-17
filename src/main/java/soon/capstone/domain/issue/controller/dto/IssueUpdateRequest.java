package soon.capstone.domain.issue.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import soon.capstone.domain.issue.service.dto.request.IssueUpdateServiceRequest;

import java.util.List;

@Builder
public record IssueUpdateRequest(

    @NotBlank(message = "오가니제이션명은 필수 입력 값입니다.")
    String organizationName,

    @NotBlank(message = "리포지토리명은 필수 입력 값입니다.")
    String repositoryName,

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    String title,

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    String content,

    @NotNull(message = "라벨 리스트는 필수 입력 값입니다.")
    List<String> labels,

    @NotBlank(message = "담당자는 필수 입력 값입니다.")
    String assignees,

    @NotBlank(message = "상태는 필수 입력 값입니다.")
    String state,

    @NotNull(message = "팀멤버 ID는 필수 입력 값입니다.")
    @Positive(message = "팀멤버 ID는 1 이상의 값이어야 합니다.")
    Long teamMemberId,

    @NotNull(message = "마일스톤 ID는 필수 입력 값입니다.")
    @Positive(message = "마일스톤 ID는 1 이상의 값이어야 합니다.")
    Long milestoneId

) {

    public IssueUpdateServiceRequest toServiceRequest(
        Long memberId,
        Long teamId,
        Long issueId
    ) {
        return IssueUpdateServiceRequest.builder()
            .memberId(memberId)
            .teamId(teamId)
            .issueId(issueId)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .title(title)
            .content(content)
            .labels(labels)
            .assignees(assignees)
            .state(state)
            .milestoneId(milestoneId)
            .build();
    }

}
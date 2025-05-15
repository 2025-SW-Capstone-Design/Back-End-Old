package soon.capstone.domain.issue.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import soon.capstone.domain.issue.controller.dto.IssueUpdateStatusRequest;
import soon.capstone.domain.issue.controller.dto.IssueCreateRequest;
import soon.capstone.domain.issue.controller.dto.IssueUpdateRequest;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;
import soon.capstone.domain.issue.service.dto.response.IssueDetailWrapperResponse;
import soon.capstone.global.swagger.annotation.ApiExceptions;

import java.util.List;

import static soon.capstone.global.exception.dto.ErrorDetail.*;

@Tag(name = "Issue API", description = "이슈 관련 API")
public interface IssueControllerDocs {

    @Operation(
        summary = "이슈 생성",
        description = """
            **사용 목적**:
            - 새로운 이슈를 생성합니다.
            
            **요청 방법**:
            - HTTP `POST` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}/projects/{projectId}/issues`
            
            **주요 사항**:
            - 요청 본문에 이슈 생성 정보를 포함해야 합니다.
            - 요청 URL의 `{teamId}`와 `{projectId}`는 각각 팀 ID와 프로젝트 ID를 나타냅니다.
            """
    )
    @RequestBody(
        description = "이슈 생성 요청 정보",
        required = true,
        content = @Content(schema = @Schema(implementation = IssueCreateRequest.class))
    )
    @Parameters({
        @Parameter(name = "teamId", description = "팀 ID", in = ParameterIn.PATH),
        @Parameter(name = "projectId", description = "프로젝트 ID", in = ParameterIn.PATH)
    })
    @ApiResponse(responseCode = "200", description = "이슈 생성 성공",
        content = @Content(schema = @Schema(implementation = Long.class)))
    @ApiExceptions({
        TEAM_NOT_FOUND,
        MEMBER_NOT_FOUND,
        TEAM_MEMBER_NOT_FOUND,
        PROJECT_NOT_FOUND,
        MILESTONE_NOT_FOUND,
        TEAM_NOT_AUTHORIZED,
        GITHUB_HTTP_CLIENT_ERROR,
        UNAUTHORIZED,
        ISSUE_LABEL_NOT_FOUND
    })
    ResponseEntity<Long> createIssue(
        IssueCreateRequest request,
        Long memberId,
        Long teamId,
        Long projectId
    );

    @Operation(
        summary = "이슈 수정",
        description = """
            **사용 목적**:
            - 기존 이슈의 정보를 수정합니다.
            
            **요청 방법**:
            - HTTP `PATCH` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}/issues/{issueId}`
            
            **주요 사항**:
            - 요청 본문에 이슈 수정 정보를 포함해야 합니다.
            - 요청 URL의 `{teamId}`와 `{issueId}`는 각각 팀 ID와 이슈 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(name = "teamId", description = "팀 ID", in = ParameterIn.PATH),
        @Parameter(name = "issueId", description = "이슈 ID", in = ParameterIn.PATH)
    })
    @ApiResponse(responseCode = "200", description = "이슈 수정 성공"
        , content = @Content(schema = @Schema(implementation = Void.class)))
    @ApiExceptions({
        TEAM_NOT_FOUND,
        MEMBER_NOT_FOUND,
        TEAM_MEMBER_NOT_FOUND,
        MILESTONE_NOT_FOUND,
        ISSUE_NOT_FOUND,
        ISSUE_LABEL_NOT_FOUND,
        TEAM_NOT_AUTHORIZED,
        GITHUB_HTTP_CLIENT_ERROR
    })
    ResponseEntity<Void> updateIssue(
        IssueUpdateRequest request,
        Long memberId,
        Long teamId,
        Long issueId
    );

    @Operation(
        summary = "이슈 상태 변경",
        description = """
            **사용 목적**:
            - 이슈의 상태를 변경합니다.
            
            **요청 방법**:
            - HTTP `PATCH` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}/issues/{issueId}/closed`
            
            **주요 사항**:
            - 요청 본문에 이슈 상태 변경 정보를 포함해야 합니다.
            - 요청 URL의 `{teamId}`와 `{issueId}`는 각각 팀 ID와 이슈 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(name = "teamId", description = "팀 ID", in = ParameterIn.PATH),
        @Parameter(name = "issueId", description = "이슈 ID", in = ParameterIn.PATH)
    })
    @ApiResponse(responseCode = "200", description = "이슈 상태 변경 성공"
        , content = @Content(schema = @Schema(implementation = Void.class)))
    @ApiExceptions({
        TEAM_NOT_FOUND,
        MEMBER_NOT_FOUND,
        ISSUE_NOT_FOUND,
        TEAM_NOT_AUTHORIZED,
        GITHUB_HTTP_CLIENT_ERROR
    })
    ResponseEntity<Void> updateIssueStatus(
        IssueUpdateStatusRequest request,
        Long memberId,
        Long teamId,
        Long issueId
    );

    @Operation(
        summary = "이슈 상세 조회",
        description = """
            **사용 목적**:
            - 특정 이슈의 상세 정보를 조회합니다.
            
            **요청 방법**:
            - HTTP `GET` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}/projects/{projectId}/issues/{issueId}`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`, `{projectId}` 및 `{issueId}`는 각각 팀 ID, 프로젝트 ID 및 이슈 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(name = "teamId", description = "팀 ID", in = ParameterIn.PATH),
        @Parameter(name = "projectId", description = "프로젝트 ID", in = ParameterIn.PATH),
        @Parameter(name = "issueId", description = "이슈 ID", in = ParameterIn.PATH)
    })
    @ApiResponse(responseCode = "200", description = "이슈 상세 조회 성공",
        content = @Content(schema = @Schema(implementation = IssueDetailResponse.class)))
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TEAM_NOT_FOUND,
        PROJECT_NOT_FOUND,
        ISSUE_NOT_FOUND,
        TEAM_NOT_AUTHORIZED,
        GITHUB_HTTP_CLIENT_ERROR
    })
    ResponseEntity<IssueDetailWrapperResponse> getIssueDetail(
        Long memberId,
        Long teamId,
        Long issueId,
        Long projectId
    );

    @Operation(
        summary = "이슈 목록 조회",
        description = """
            **사용 목적**:
            - 특정 팀의 이슈 목록을 조회합니다.
            
            **요청 방법**:
            - HTTP `GET` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}/projects/{projectId}/issues?scope={scope}`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`와 `{projectId}`는 각각 팀 ID와 프로젝트 ID를 나타냅니다.
            - `scope` 파라미터는 이슈 목록의 범위를 지정합니다. (예: `PROJECT(project)`, TEAM(team))
            """
    )
    @Parameters({
        @Parameter(name = "teamId", description = "팀 ID", in = ParameterIn.PATH),
        @Parameter(name = "projectId", description = "프로젝트 ID", in = ParameterIn.PATH),
        @Parameter(name = "scope", description = "이슈 목록 범위 PROJECT(project), TEAM(team)", in = ParameterIn.QUERY)
    })
    @ApiResponse(responseCode = "200", description = "이슈 목록 조회 성공",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = IssueDetailResponse.class))))
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TEAM_NOT_FOUND,
        PROJECT_NOT_FOUND,
        TEAM_NOT_AUTHORIZED,
        INVALID_REQUEST,
        GITHUB_HTTP_CLIENT_ERROR
    })
    ResponseEntity<List<IssueDetailResponse>> getIssues(
        Long memberId,
        Long teamId,
        Long projectId,
        String scope
    );
}


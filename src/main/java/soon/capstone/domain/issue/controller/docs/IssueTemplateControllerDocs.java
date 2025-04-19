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
import soon.capstone.domain.issue.controller.dto.IssueTemplateCreateRequest;
import soon.capstone.domain.issue.controller.dto.IssueTemplateUpdateRequest;
import soon.capstone.domain.issue.service.dto.response.IssueTemplateDetailResponse;
import soon.capstone.global.exception.dto.ErrorDetail;
import soon.capstone.global.swagger.annotation.ApiExceptions;

import java.util.List;

import static soon.capstone.global.exception.dto.ErrorDetail.*;

@Tag(name = "IssueTemplate API", description = "이슈 템플릿 관련 API")
public interface IssueTemplateControllerDocs {

    @Operation(
        summary = "이슈 템플릿 생성",
        description = """
            **사용 목적**:
            - 새로운 이슈 템플릿을 생성합니다.
            
            **요청 방법**:
            - HTTP `POST` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}/issue-templates`
            
            **주요 사항**:
            - 요청 본문에 이슈 템플릿 생성 정보를 포함해야 합니다.
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            """
    )
    @RequestBody(
        description = "이슈 템플릿 생성 요청 정보",
        required = true,
        content = @Content(
            schema = @Schema(implementation = IssueTemplateCreateRequest.class)
        )
    )
    @Parameters({
        @Parameter(
            name = "teamId",
            description = "팀 ID",
            in = ParameterIn.PATH
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "이슈 템플릿 생성 성공",
        content = @Content(
            schema = @Schema(implementation = Long.class)
        )
    )
    @ApiExceptions({
        TEAM_NOT_FOUND,
        MEMBER_NOT_FOUND,
        PROJECT_NOT_FOUND,
        TEAM_NOT_AUTHORIZED,
        ISSUE_LABEL_ALREADY_EXISTS
    })
    ResponseEntity<Long> createIssueTemplate(
        IssueTemplateCreateRequest request,
        Long teamId,
        Long memberId
    );

    @Operation(
        summary = "이슈 템플릿 수정",
        description = """
            **사용 목적**:
            - 기존 이슈 템플릿을 수정합니다.
            
            **요청 방법**:
            - HTTP `PATCH` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}/issue-templates/{issueTemplateId}`
            
            **주요 사항**:
            - 요청 본문에 이슈 템플릿 수정 정보를 포함해야 합니다.
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            - 요청 URL의 `{issueTemplateId}`는 수정할 템플릿의 ID를 나타냅니다.
            """
    )
    @RequestBody(
        description = "이슈 템플릿 수정 요청 정보",
        required = true,
        content = @Content(
            schema = @Schema(implementation = IssueTemplateUpdateRequest.class)
        )
    )
    @Parameters({
        @Parameter(
            name = "teamId",
            description = "팀 ID",
            in = ParameterIn.PATH
        ),
        @Parameter(
            name = "issueTemplateId",
            description = "이슈 템플릿 ID",
            in = ParameterIn.PATH
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "이슈 템플릿 수정 성공",
        content = @Content(
            schema = @Schema(implementation = Void.class)
        )
    )
    @ApiExceptions({
        TEAM_NOT_FOUND,
        MEMBER_NOT_FOUND,
        PROJECT_NOT_FOUND,
        ISSUE_NOT_FOUND,
        TEAM_NOT_AUTHORIZED,
        ISSUE_TEMPLATE_ALREADY_EXISTS
    })
    ResponseEntity<Void> updateIssueTemplate(
        IssueTemplateUpdateRequest request,
        Long teamId,
        Long issueTemplateId,
        Long memberId
    );

    @Operation(
        summary = "이슈 템플릿 조회",
        description = """
            **사용 목적**:
            - 특정 이슈 템플릿을 조회합니다.
            
            **요청 방법**:
            - HTTP `GET` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}/issue-templates/{issueTemplateId}`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            - 요청 URL의 `{issueTemplateId}`는 조회할 템플릿의 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(
            name = "teamId",
            description = "팀 ID",
            in = ParameterIn.PATH
        ),
        @Parameter(
            name = "issueTemplateId",
            description = "이슈 템플릿 ID",
            in = ParameterIn.PATH
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "이슈 템플릿 조회 성공",
        content = @Content(
            schema = @Schema(implementation = IssueTemplateDetailResponse.class)
        )
    )
    @ApiExceptions({
        TEAM_NOT_FOUND,
        MEMBER_NOT_FOUND,
        ISSUE_NOT_FOUND,
        TEAM_NOT_AUTHORIZED
    })
    ResponseEntity<IssueTemplateDetailResponse> getIssueTemplate(
        Long teamId,
        Long issueTemplateId,
        Long memberId
    );

    @Operation(
        summary = "이슈 템플릿 목록 조회",
        description = """
            **사용 목적**:
            - 특정 프로젝트에 대한 이슈 템플릿 목록을 조회합니다.
            
            **요청 방법**:
            - HTTP `GET` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}/issue-templates/projects/{projectId}`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            - 요청 URL의 `{projectId}`는 조회할 프로젝트의 ID를 나타냅니다.
            - 쿼리 파라미터 `type`은 이슈 템플릿의 유형을 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(
            name = "teamId",
            description = "팀 ID",
            in = ParameterIn.PATH
        ),
        @Parameter(
            name = "projectId",
            description = "프로젝트 ID",
            in = ParameterIn.PATH
        ),
        @Parameter(
            name = "type",
            description = "이슈 템플릿 유형",
            in = ParameterIn.QUERY
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "이슈 템플릿 목록 조회 성공",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = IssueTemplateDetailResponse.class)
            )
        )
    )
    @ApiExceptions({
        TEAM_NOT_FOUND,
        MEMBER_NOT_FOUND,
        PROJECT_NOT_FOUND,
        TEAM_NOT_AUTHORIZED
    })
    ResponseEntity<List<IssueTemplateDetailResponse>> getIssueTemplates(
        Long teamId,
        Long projectId,
        String type,
        Long memberId
    );

    @Operation(
        summary = "이슈 템플릿 삭제",
        description = """
            **사용 목적**:
            - 특정 이슈 템플릿을 삭제합니다.
            
            **요청 방법**:
            - HTTP `DELETE` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}/issue-templates/{issueTemplateId}`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            - 요청 URL의 `{issueTemplateId}`는 삭제할 템플릿의 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(
            name = "teamId",
            description = "팀 ID",
            in = ParameterIn.PATH
        ),
        @Parameter(
            name = "issueTemplateId",
            description = "이슈 템플릿 ID",
            in = ParameterIn.PATH
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "이슈 템플릿 삭제 성공",
        content = @Content(
            schema = @Schema(implementation = Void.class)
        )
    )
    @ApiExceptions({
        TEAM_NOT_FOUND,
        MEMBER_NOT_FOUND,
        TEAM_NOT_AUTHORIZED
    })
    ResponseEntity<Void> deleteIssueTemplate(
        Long teamId,
        Long issueTemplateId,
        Long memberId
    );
}

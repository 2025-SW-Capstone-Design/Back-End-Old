package soon.capstone.domain.milestone.controller.docs;

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
import soon.capstone.domain.milestone.controller.dto.request.MilestoneCreateRequest;
import soon.capstone.domain.milestone.controller.dto.request.MilestoneUpdateRequest;
import soon.capstone.domain.milestone.service.dto.response.MilestoneDetailResponse;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.global.swagger.annotation.ApiExceptions;

import java.util.List;

import static soon.capstone.global.exception.dto.ErrorDetail.*;

@Tag(name = "Milestone API", description = "마일스톤 관련 API")
public interface MilestoneControllerDocs {

    @Operation(
        summary = "마일스톤 조회",
        description = """
            **사용 목적**:
            - 팀에 속한 마일스톤을 조회합니다.
            
            **요청 방법**:
            - HTTP `GET` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(name = "teamId", description = "팀 ID", in = ParameterIn.PATH)
    })
    @ApiResponse(
        responseCode = "200",
        description = "마일스톤 조회 성공",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = MilestoneResponse.class)
            )
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TEAM_NOT_FOUND,
        TEAM_NOT_AUTHORIZED,

    })
    ResponseEntity<List<MilestoneResponse>> getMilestonesByTeam(
        Long memberId,
        Long teamId
    );

    @Operation(
        summary = "마일스톤 생성",
        description = """
            **사용 목적**:
            - 새로운 마일스톤을 생성합니다.
            
            **요청 방법**:
            - HTTP `POST` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}/projects/{projectId}`
            
            **주요 사항**:
            - 요청 본문에 마일스톤 생성 정보를 포함해야 합니다.
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            - 요청 URL의 `{projectId}`는 프로젝트 ID를 나타냅니다.
            """
    )
    @RequestBody(
        description = "마일스톤 생성 요청 정보",
        required = true,
        content = @Content(schema = @Schema(implementation = MilestoneCreateRequest.class))
    )
    @Parameters({
        @Parameter(name = "teamId", description = "팀 ID", in = ParameterIn.PATH),
        @Parameter(name = "projectId", description = "프로젝트 ID", in = ParameterIn.PATH)
    })
    @ApiResponse(
        responseCode = "200",
        description = "마일스톤 생성 성공",
        content = @Content(
            schema = @Schema(implementation = Long.class)
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TEAM_NOT_FOUND,
        PROJECT_NOT_FOUND,
        MILESTONE_INVALID_DATE,
        MILESTONE_DUPLICATE_TITLE,
        TEAM_NOT_AUTHORIZED,
        GITHUB_HTTP_CLIENT_ERROR
    })
    ResponseEntity<Long> createMilestone(
        Long memberId,
        Long teamId,
        Long projectId,
        MilestoneCreateRequest milestoneCreateRequest
    );

    @Operation(
        summary = "프로젝트에 속한 마일스톤 조회",
        description = """
            **사용 목적**:
            - 특정 프로젝트에 속한 마일스톤을 조회합니다.
            
            **��청 방법**:
            - HTTP `GET` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}/projects/{projectId}`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            - 요청 URL의 `{projectId}`는 프로젝트 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(name = "teamId", description = "팀 ID", in = ParameterIn.PATH),
        @Parameter(name = "projectId", description = "프로젝트 ID", in = ParameterIn.PATH)
    })
    @ApiResponse(
        responseCode = "200",
        description = "프로젝트에 속한 마일스톤 조회 성공",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = MilestoneResponse.class)
            )
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TEAM_NOT_FOUND,
        PROJECT_NOT_FOUND,
        TEAM_NOT_AUTHORIZED
    })
    ResponseEntity<List<MilestoneResponse>> getMilestonesByProject(
        Long memberId,
        Long teamId,
        Long projectId
    );

    @Operation(
        summary = "마일스톤 상세 조회",
        description = """
            **사용 목적**:
            - 특정 마일스톤의 상세 정보를 조회합니다.
            
            **요청 방법**:
            - HTTP `GET` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}/milestones/{milestoneId}`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            - 요청 URL의 `{milestoneId}`는 마일스톤 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(name = "teamId", description = "팀 ID", in = ParameterIn.PATH),
        @Parameter(name = "milestoneId", description = "마일스톤 ID", in = ParameterIn.PATH)
    })
    @ApiResponse(
        responseCode = "200",
        description = "마일스톤 상세 조회 성공",
        content = @Content(
            schema = @Schema(implementation = MilestoneDetailResponse.class)
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TEAM_NOT_FOUND,
        MILESTONE_NOT_FOUND,
        TEAM_NOT_AUTHORIZED
    })
    ResponseEntity<MilestoneDetailResponse> getMilestoneDetail(
        Long memberId,
        Long teamId,
        Long milestoneId
    );

    @Operation(
        summary = "마일스톤 수정",
        description = """
            **사용 목적**:
            - 특정 마일스톤의 정보를 수정합니다.
            
            **요청 방법**:
            - HTTP `PUT` 메서드 사용
            - 요청 URL: `/api/v1/teams/{teamId}/projects/{projectId}/milestones/{milestoneId}`
            
            **주요 사항**:
            - 요청 본문에 마일스톤 수정 정보를 포함해야 합니다.
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            - 요청 URL의 `{projectId}`는 프로젝트 ID를 나타냅니다.
            - 요청 URL의 `{milestoneId}`는 마일스톤 ID를 나타냅니다.
            """
    )
    @RequestBody(
        description = "마일스톤 수정 요청 정보",
        required = true,
        content = @Content(schema = @Schema(implementation = MilestoneUpdateRequest.class))
    )
    @Parameters({
        @Parameter(name = "teamId", description = "팀 ID", in = ParameterIn.PATH),
        @Parameter(name = "projectId", description = "프로젝트 ID", in = ParameterIn.PATH),
        @Parameter(name = "milestoneId", description = "마일스톤 ID", in = ParameterIn.PATH)
    })
    @ApiResponse(
        responseCode = "200",
        description = "마일스톤 수정 성공",
        content = @Content(
            schema = @Schema(implementation = MilestoneResponse.class)
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TEAM_NOT_FOUND,
        PROJECT_NOT_FOUND,
        MILESTONE_NOT_FOUND,
        MILESTONE_INVALID_DATE,
        MILESTONE_DUPLICATE_TITLE,
        TEAM_NOT_AUTHORIZED,
        GITHUB_HTTP_CLIENT_ERROR
    })
    ResponseEntity<MilestoneResponse> updateMilestone(
        Long memberId,
        Long teamId,
        Long projectId,
        Long milestoneId,
        MilestoneUpdateRequest milestoneUpdateRequest
    );
}

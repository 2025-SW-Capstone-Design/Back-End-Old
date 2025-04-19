package soon.capstone.domain.readme.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import soon.capstone.domain.readme.controller.dto.request.ReadmeCreateRequest;
import soon.capstone.domain.readme.controller.dto.request.ReadmeUpdateRequest;
import soon.capstone.domain.readme.service.dto.response.ReadmeDetailResponse;
import soon.capstone.domain.readme.service.dto.response.ReadmeListResponse;
import soon.capstone.global.exception.dto.ErrorDetail;
import soon.capstone.global.swagger.annotation.ApiExceptions;

import java.util.List;

import static soon.capstone.global.exception.dto.ErrorDetail.*;

@Tag(name = "Readme API", description = "Readme 관련 API")
public interface ReadmeControllerDocs {

    @Operation(
        summary = "리드미 생성",
        description = """
            **사용 목적**:
            - 리드미를 생성합니다..
            
            **요청 방법**:
            - HTTP `POST` 메서드 사용
            - URL: `/api/v1/teams/{teamId}/projects/{projectId}/readmes`
            
            **주요 사항**:
            - 요청 본문에 리드미 생성 정보를 포함해야 합니다.
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            - 요청 URL의 `{projectId}`는 프로젝트 ID를 나타냅니다.
            """
    )
    @RequestBody(
        description = "리드미 생성 요청 정보",
        required = true,
        content = @Content(schema = @Schema(implementation = ReadmeCreateRequest.class))
    )
    @Parameters({
        @Parameter(
            name = "teamId",
            description = "팀 ID",
            required = true,
            schema = @Schema(type = "integer")
        ),
        @Parameter(
            name = "projectId",
            description = "프로젝트 ID",
            required = true,
            schema = @Schema(type = "integer")
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "리드미 생성 성공",
        content = @Content(
            schema = @Schema(type = "integer", example = "1")
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TEAM_NOT_FOUND,
        PROJECT_NOT_FOUND,
        TEAM_NOT_AUTHORIZED
    })
    ResponseEntity<Long> createReadme(
        ReadmeCreateRequest request,
        Long memberId,
        Long teamId,
        Long projectId
    );

    @Operation(
        summary = "리드미 수정",
        description = """
            **사용 목적**:
            - 리드미를 수정합니다.
            
            **요청 방법**:
            - HTTP `PATCH` 메서드 사용
            - URL: `/api/v1/teams/{teamId}/projects/{projectId}/readmes/{readmeId}`
            
            **주요 사항**:
            - 요청 본문에 리드미 수정 정보를 포함해야 합니다.
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            - 요청 URL의 `{projectId}`는 프로젝트 ID를 나타냅니다.
            - 요청 URL의 `{readmeId}`는 리드미 ID를 나타냅니다.
            """
    )
    @RequestBody(
        description = "리드미 수정 요청 정보",
        required = true,
        content = @Content(schema = @Schema(implementation = ReadmeUpdateRequest.class))
    )
    @Parameters({
        @Parameter(
            name = "teamId",
            description = "팀 ID",
            required = true,
            schema = @Schema(type = "integer")
        ),
        @Parameter(
            name = "projectId",
            description = "프로젝트 ID",
            required = true,
            schema = @Schema(type = "integer")
        ),
        @Parameter(
            name = "readmeId",
            description = "리드미 ID",
            required = true,
            schema = @Schema(type = "integer")
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "리드미 수정 성공",
        content = @Content(
            schema = @Schema(type = "integer", example = "1")
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TEAM_NOT_FOUND,
        PROJECT_NOT_FOUND,
        TEAM_NOT_AUTHORIZED
    })
    ResponseEntity<Long> updateReadme(
        ReadmeUpdateRequest request,
        Long memberId,
        Long teamId,
        Long projectId,
        Long readmeId
    );

    @Operation(
        summary = "리드미 삭제",
        description = """
            **사용 목적**:
            - 리드미를 삭제합니다.
            
            **요청 방법**:
            - HTTP `DELETE` 메서드 사용
            - URL: `/api/v1/teams/{teamId}/readmes/{readmeId}`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            - 요청 URL의 `{readmeId}`는 리드미 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(
            name = "teamId",
            description = "팀 ID",
            required = true,
            schema = @Schema(type = "integer")
        ),
        @Parameter(
            name = "readmeId",
            description = "리드미 ID",
            required = true,
            schema = @Schema(type = "integer")
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "리드미 삭제 성공"
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TEAM_NOT_FOUND,
        READEME_NOT_FOUND,
        TEAM_NOT_AUTHORIZED
    })
    ResponseEntity<Void> deleteReadme(
        Long memberId,
        Long teamId,
        Long readmeId
    );

    @Operation(
        summary = "리드미 목록 조회",
        description = """
            **사용 목적**:
            - 리드미 목록을 조회합니다.
            
            **요청 방법**:
            - HTTP `GET` 메서드 사용
            - URL: `/api/v1/teams/{teamId}/projects/{projectId}/readmes`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            - 요청 URL의 `{projectId}`는 프로젝트 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(
            name = "teamId",
            description = "팀 ID",
            required = true,
            schema = @Schema(type = "integer")
        ),
        @Parameter(
            name = "projectId",
            description = "프로젝트 ID",
            required = true,
            schema = @Schema(type = "integer")
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "리드미 목록 조회 성공",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = ReadmeListResponse.class)
            )
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TEAM_NOT_FOUND,
        TEAM_NOT_AUTHORIZED
    })
    ResponseEntity<List<ReadmeListResponse>> getReadmeList(
        Long memberId,
        Long teamId,
        Long projectId
    );

    @Operation(
        summary = "리드미 상세 조회",
        description = """
            **사용 목적**:
            - 리드미의 상세 정보를 조회합니다.
            
            **요청 방법**:
            - HTTP `GET` 메서드 사용
            - URL: `/api/v1/teams/{teamId}/readmes/{readmeId}`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            - 요청 URL의 `{readmeId}`는 리드미 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(
            name = "teamId",
            description = "팀 ID",
            required = true,
            schema = @Schema(type = "integer")
        ),
        @Parameter(
            name = "readmeId",
            description = "리드미 ID",
            required = true,
            schema = @Schema(type = "integer")
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "리드미 상세 조회 성공",
        content = @Content(
            schema = @Schema(implementation = ReadmeDetailResponse.class)
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TEAM_NOT_FOUND,
        READEME_NOT_FOUND,
        TEAM_NOT_AUTHORIZED
    })
    ResponseEntity<ReadmeDetailResponse> getReadmeDetail(
        Long memberId,
        Long teamId,
        Long readmeId
    );

    @Operation(
        summary = "리드미 롤백",
        description = """
            **사용 목적**:
            - 리드미를 이전 버전으로 롤백합니다.
            
            **요청 방법**:
            - HTTP `POST` 메서드 사용
            - URL: `/api/v1/teams/{teamId}/projects/{projectId}/readmes/{readmeId}/rollback`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            - 요청 URL의 `{projectId}`는 프로젝트 ID를 나타냅니다.
            - 요청 URL의 `{readmeId}`는 리드미 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(
            name = "teamId",
            description = "팀 ID",
            required = true,
            schema = @Schema(type = "integer")
        ),
        @Parameter(
            name = "projectId",
            description = "프로젝트 ID",
            required = true,
            schema = @Schema(type = "integer")
        ),
        @Parameter(
            name = "readmeId",
            description = "리드미 ID",
            required = true,
            schema = @Schema(type = "integer")
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "리드미 롤백 성공",
        content = @Content(
            schema = @Schema(type = "integer", example = "1")
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TEAM_NOT_FOUND,
        PROJECT_NOT_FOUND,
        READEME_NOT_FOUND,
        TEAM_NOT_AUTHORIZED
    })
    ResponseEntity<Long> rollback(
        Long memberId,
        Long teamId,
        Long projectId,
        Long readmeId
    );
}

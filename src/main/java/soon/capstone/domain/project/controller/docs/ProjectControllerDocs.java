package soon.capstone.domain.project.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import soon.capstone.domain.project.service.dto.response.ProjectDetailResponse;
import soon.capstone.global.exception.dto.ErrorDetail;
import soon.capstone.global.swagger.annotation.ApiExceptions;

import java.util.List;

import static soon.capstone.global.exception.dto.ErrorDetail.*;

@Tag(name = "Project API", description = "Project 관련 API")
public interface ProjectControllerDocs {

    @Operation(
        summary = "팀 프로젝트(레포지토리) 목록 조회",
        description = """
            **사용 목적**:
            - 팀 프로젝트(레포지토리) 목록을 조회합니다.
            
            **요청 방법**:
            - HTTP `GET` 메서드 사용
            - URL: `/api/v1/projects/{teamId}`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(
            name = "teamId",
            description = "팀 ID",
            required = true
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "팀 프로젝트(레포지토리) 목록 조회 성공",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = ProjectDetailResponse.class)
            )
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TEAM_NOT_FOUND,
        TEAM_MEMBER_NOT_FOUND
    })
    ResponseEntity<List<ProjectDetailResponse>> getProjects(Long memberId, Long teamId);
}

package soon.capstone.domain.teammember.controller.docs;

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
import soon.capstone.domain.teammember.controller.dto.TeamMemberUpdateRoleRequest;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;
import soon.capstone.global.swagger.annotation.ApiExceptions;

import java.util.List;

import static soon.capstone.global.exception.dto.ErrorDetail.*;

@Tag(name = "TeamMember API", description = "팀원 관련 API")
public interface TeamMemberControllerDocs {

    @Operation(
        summary = "팀원 조회",
        description = """
            **사용 목적**:
            - 팀원을 조회합니다.
            
            **요청 방법**:
            - HTTP `GET` 메서드 사용
            - URL: `/api/v1/teams/{teamId}/members`
            
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
        description = "팀원 조회 성공",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = TeamMemberDetailResponse.class)
            )
        ))
    @ApiExceptions({
        TEAM_NOT_FOUND,
        MEMBER_NOT_FOUND,
        TEAM_NOT_AUTHORIZED
    })
    ResponseEntity<List<TeamMemberDetailResponse>> getTeamMembers(
        Long teamId,
        Long memberId
    );

    @Operation(
        summary = "팀원 역할 변경",
        description = """
            **사용 목적**:
            - 팀원의 역할을 변경합니다.
            
            **요청 방법**:
            - HTTP `PATCH` 메서드 사용
            - URL: `/api/v1/teams/{teamId}/members`
            
            **주요 사항**:
            - 요청 본문에 팀원 역할 변경 정보를 포함해야 합니다.
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            """
    )
    @RequestBody(
        description = "팀원 역할 변경 요청 정보",
        required = true,
        content = @Content(schema = @Schema(implementation = TeamMemberUpdateRoleRequest.class))
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
        description = "팀원 역할 변경 성공"
    )
    @ApiExceptions({
        TEAM_MEMBER_NOT_FOUND,
        IS_NOT_TEAM_LEADER,
        INVALID_REQUEST
    })
    ResponseEntity<Void> updateTeamMemberRole(
        Long teamId,
        TeamMemberUpdateRoleRequest request,
        Long memberId
    );
}

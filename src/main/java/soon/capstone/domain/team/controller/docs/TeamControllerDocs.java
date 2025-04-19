package soon.capstone.domain.team.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import soon.capstone.domain.team.controller.dto.TeamCreateRequest;
import soon.capstone.domain.team.controller.dto.TeamInvitationRequest;
import soon.capstone.domain.team.controller.dto.TeamJoinRequest;
import soon.capstone.global.swagger.annotation.ApiExceptions;

import static soon.capstone.global.exception.dto.ErrorDetail.*;

@Tag(name = "Team API", description = "팀 관련 API")
public interface TeamControllerDocs {

    @Operation(
        summary = "팀 생성",
        description = """
            **사용 목적**:
            - 팀을 생성합니다..
            
            **요청 방법**:
            - HTTP `POST` 메서드 사용
            - URL: `/api/v1/teams`
            
            **주요 사항**:
            - 요청 본문에 팀 생성 정보를 포함해야 합니다.
            """
    )
    @RequestBody(
        description = "팀 생성 요청 정보",
        required = true,
        content = @Content(schema = @Schema(implementation = TeamCreateRequest.class))
    )
    @ApiResponse(
        responseCode = "200",
        description = "팀 생성 성공",
        content = @Content(
            schema = @Schema(implementation = Long.class)
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        TOKEN_NOT_FOUND,
        TEAM_ALREADY_EXISTS,
        GITHUB_HTTP_CLIENT_ERROR
    })
    ResponseEntity<Long> createTeam(
        TeamCreateRequest request,
        Long memberId
    );

    @Operation(
        summary = "초대 코드 생성",
        description = """
            **사용 목적**:
            - 팀 초대 코드를 생성합니다..
            
            **요청 방법**:
            - HTTP `POST` 메서드 사용
            - URL: `/api/v1/teams/{teamId}/invitation-code`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(
            name = "teamId",
            description = "팀 ID",
            required = true,
            schema = @Schema(type = "integer")
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "초대 코드 생성 성공",
        content = @Content(
            schema = @Schema(implementation = String.class)
        )
    )
    @ApiExceptions({
        TEAM_MEMBER_NOT_FOUND,
        IS_NOT_TEAM_LEADER,
        INVITATION_CODE_NOT_FOUND
    })
    ResponseEntity<String> generateInvitationCode(
        Long memberId,
        Long teamId
    );

    @Operation(
        summary = "초대 이메일 전송",
        description = """
            **사용 목적**:
            - 팀 초대 이메일을 전송합니다..
            
            **요청 방법**:
            - HTTP `POST` 메서드 사용
            - URL: `/api/v1/teams/{teamId}/invitation-emails`
            
            **주요 사항**:
            - 요청 URL의 `{teamId}`는 팀 ID를 나타냅니다.
            """
    )
    @RequestBody(
        description = "초대 이메일 요청 정보",
        required = true,
        content = @Content(schema = @Schema(implementation = TeamInvitationRequest.class))
    )
    @Parameters({
        @Parameter(
            name = "teamId",
            description = "팀 ID",
            required = true,
            schema = @Schema(type = "integer")
        )
    })
    @ApiResponse(
        responseCode = "200",
        description = "초대 이메일 전송 성공"
    )
    @ApiExceptions({
        TEAM_MEMBER_NOT_FOUND,
        IS_NOT_TEAM_LEADER,
        INVITATION_CODE_NOT_FOUND,
        EMAIL_SEND
    })
    ResponseEntity<Void> sendInvitationEmails(
        TeamInvitationRequest request,
        Long memberId,
        Long teamId
    );

    @Operation(
        summary = "팀 가입",
        description = """
            **사용 목적**:
            - 팀에 가입합니다..
            
            **요청 방법**:
            - HTTP `POST` 메서드 사용
            - URL: `/api/v1/teams/join`
            
            **주요 사항**:
            - 요청 본문에 팀 가입 정보를 포함해야 합니다.
            """
    )
    @RequestBody(
        description = "팀 가입 요청 정보",
        required = true,
        content = @Content(schema = @Schema(implementation = TeamJoinRequest.class))
    )
    @ApiResponse(
        responseCode = "200",
        description = "팀 가입 성공",
        content = @Content(
            schema = @Schema(implementation = Long.class)
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        INVITATION_CODE_NOT_FOUND,
        TEAM_NOT_FOUND,
        TEAM_MEMBER_ALREADY_EXISTS,
        TEAM_MEMBER_NOT_FOUND,
        GITHUB_HTTP_CLIENT_ERROR
    })
    ResponseEntity<Long> joinTeam(
        TeamJoinRequest request,
        Long memberId
    );
}

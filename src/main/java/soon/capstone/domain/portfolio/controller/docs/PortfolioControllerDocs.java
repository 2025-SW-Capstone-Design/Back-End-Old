package soon.capstone.domain.portfolio.controller.docs;

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
import soon.capstone.domain.portfolio.controller.dto.request.PortfolioCreateRequest;
import soon.capstone.domain.portfolio.controller.dto.request.PortfolioUpdateRequest;
import soon.capstone.domain.portfolio.controller.dto.response.PortfolioResponse;
import soon.capstone.global.exception.dto.ErrorDetail;
import soon.capstone.global.swagger.annotation.ApiExceptions;

import static soon.capstone.global.exception.dto.ErrorDetail.*;

@Tag(name = "Portfolio API", description = "포트폴리오 관련 API")
public interface PortfolioControllerDocs {

    @Operation(
        summary = "사용자 포트폴리오 목록 조회",
        description = """
            **사용 목적**:
            - 사용자의 포트폴리오 목록을 조회합니다.
            
            **요청 방법**:
            - HTTP `GET` 메서드 사용
            - 요청 URL: `/api/v1/portfolios`
            """
    )
    @ApiResponse(
        responseCode = "200",
        description = "포트폴리오 목록 조회 성공",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = PortfolioResponse.class)
            )
        )
    )
    ResponseEntity<?> getPortfolios(Long memberId);

    @Operation(
        summary = "포트폴리오 상세 조회",
        description = """
            **사용 목적**:
            - 포트폴리오의 상세 정보를 조회합니다.
            
            **요청 방법**:
            - HTTP `GET` 메서드 사용
            - 요청 URL: `/api/v1/portfolios/{portfolioId}`
            
            **주요 사항**:
            - 요청 URL의 `{portfolioId}`는 포트폴리오 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(name = "portfolioId", description = "포트폴리오 ID", required = true)
    })
    @ApiResponse(
        responseCode = "200",
        description = "포트폴리오 상세 조회 성공",
        content = @Content(
            schema = @Schema(implementation = PortfolioResponse.class)
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        PORTFOLIO_NOT_FOUND,
        PORTFOLIO_IS_NOT_OWNER,
    })
    ResponseEntity<?> getPortfolio(Long memberId, Long portfolioId);

    @Operation(
        summary = "포트폴리오 생성",
        description = """
            **사용 목적**:
            - 새로운 포트폴리오를 생성합니다.
            
            **요청 방법**:
            - HTTP `POST` 메서드 사용
            - 요청 URL: `/api/v1/portfolios`
            
            **주요 사항**:
            - 요청 본문에 포트폴리오 생성 정보를 포함해야 합니다.
            """
    )
    @RequestBody(
        description = "포트폴리오 생성 요청 정보",
        required = true,
        content = @Content(schema = @Schema(implementation = PortfolioCreateRequest.class))
    )
    @ApiResponse(
        responseCode = "200",
        description = "포트폴리오 생성 성공",
        content = @Content(
            schema = @Schema(implementation = Long.class)
        )
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        PORTFOLIO_DUPLICATE_TITLE
    })
    ResponseEntity<?> createPortfolio(Long memberId, PortfolioCreateRequest portfolioCreateRequest);

    @Operation(
        summary = "포트폴리오 수정",
        description = """
            **사용 목적**:
            - 기존 포트폴리오를 수정합니다.
            
            **요청 방법**:
            - HTTP `PUT` 메서드 사용
            - 요청 URL: `/api/v1/portfolios/{portfolioId}`
            
            **주요 사항**:
            - 요청 본문에 포트폴리오 수정 정보를 포함해야 합니다.
            - 요청 URL의 `{portfolioId}`는 포트폴리오 ID를 나타냅니다.
            """
    )
    @RequestBody(
        description = "포트폴리오 수정 요청 정보",
        required = true,
        content = @Content(schema = @Schema(implementation = PortfolioUpdateRequest.class))
    )
    @Parameters({
        @Parameter(name = "portfolioId", description = "포트폴리오 ID", required = true)
    })
    @ApiResponse(
        responseCode = "200",
        description = "포트폴리오 수정 성공",
        content = @Content(
            schema = @Schema(implementation = Long.class)
        )
    )
    @ApiExceptions({
        PORTFOLIO_BAD_REQUEST,
        MEMBER_NOT_FOUND,
        PORTFOLIO_NOT_FOUND,
        PORTFOLIO_IS_NOT_OWNER,
        PORTFOLIO_DUPLICATE_TITLE
    })
    ResponseEntity<Long> updatePortfolio(Long memberId, Long portfolioId, PortfolioUpdateRequest portfolioUpdateRequest);

    @Operation(
        summary = "포트폴리오 삭제",
        description = """
            **사용 목적**:
            - 기존 포트폴리오를 삭제합니다.
            
            **요청 방법**:
            - HTTP `DELETE` 메서드 사용
            - 요청 URL: `/api/v1/portfolios/{portfolioId}`
            
            **주요 사항**:
            - 요청 URL의 `{portfolioId}`는 포트폴리오 ID를 나타냅니다.
            """
    )
    @Parameters({
        @Parameter(name = "portfolioId", description = "포트폴리오 ID", required = true)
    })
    @ApiResponse(
        responseCode = "200",
        description = "포트폴리오 삭제 성공"
    )
    @ApiExceptions({
        MEMBER_NOT_FOUND,
        PORTFOLIO_NOT_FOUND,
        PORTFOLIO_IS_NOT_OWNER
    })
    ResponseEntity<Void> deletePortfolio(Long memberId, Long portfolioId);
}

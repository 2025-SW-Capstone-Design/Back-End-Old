package soon.capstone.domain.portfolio.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.domain.portfolio.controller.dto.request.PortfolioCreateRequest;
import soon.capstone.domain.portfolio.controller.dto.request.PortfolioUpdateRequest;
import soon.capstone.domain.portfolio.controller.dto.response.PortfolioDetailResponse;
import soon.capstone.domain.portfolio.controller.dto.response.PortfolioResponse;
import soon.capstone.domain.portfolio.service.PortfolioService;
import soon.capstone.global.anootation.AuthMemberId;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios")
@RestController
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping
    public ResponseEntity<?> getPortfolios(@AuthMemberId Long memberId) {
        List<PortfolioResponse> portfolios = portfolioService.getPortfolios(memberId);
        return ResponseEntity.ok(portfolios);
    }

    @GetMapping("/{portfolioId}")
    public ResponseEntity<?> getPortfolio(
        @AuthMemberId Long memberId,
        @PathVariable Long portfolioId
    ) {
        PortfolioDetailResponse portfolio = portfolioService.getPortfolio(memberId, portfolioId);
        return ResponseEntity.ok(portfolio);
    }

    @PostMapping
    public ResponseEntity<?> createPortfolio(
        @AuthMemberId Long memberId,
        @RequestBody @Valid PortfolioCreateRequest portfolioCreateRequest
    ) {
        Long portfolioId = portfolioService.createPortfolio(memberId, portfolioCreateRequest.toServiceRequest());
        return ResponseEntity.ok(portfolioId);
    }

    @PutMapping("/{portfolioId}")
    public ResponseEntity<Long> updatePortfolio(
        @AuthMemberId Long memberId,
        @PathVariable Long portfolioId,
        @RequestBody @Valid PortfolioUpdateRequest portfolioUpdateRequest
    ) {
        Long updatedPortfolioId = portfolioService.updatePortfolio(memberId, portfolioUpdateRequest.toServiceRequest(portfolioId));
        return ResponseEntity.ok(updatedPortfolioId);
    }

    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(
        @AuthMemberId Long memberId,
        @PathVariable Long portfolioId
    ) {
        portfolioService.deletePortfolio(memberId, portfolioId);
        return ResponseEntity.noContent().build();
    }
}
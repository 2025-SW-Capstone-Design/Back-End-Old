package soon.capstone.domain.portfolio.service.dto;

import lombok.Builder;

@Builder
public record PortfolioUpdateServiceRequest(
    String title,
    String content,
    Long portfolioId
) {
}

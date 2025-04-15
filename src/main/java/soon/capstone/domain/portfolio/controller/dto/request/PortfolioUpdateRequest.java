package soon.capstone.domain.portfolio.controller.dto.request;

import lombok.Builder;
import soon.capstone.domain.portfolio.service.dto.PortfolioUpdateServiceRequest;

@Builder
public record PortfolioUpdateRequest(
    String title,
    String content
) {
    public PortfolioUpdateServiceRequest toServiceRequest(Long portfolioId) {
        return PortfolioUpdateServiceRequest.builder()
            .title(title)
            .content(content)
            .portfolioId(portfolioId)
            .build();
    }
}

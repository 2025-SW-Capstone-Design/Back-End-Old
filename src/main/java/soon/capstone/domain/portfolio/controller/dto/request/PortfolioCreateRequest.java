package soon.capstone.domain.portfolio.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import soon.capstone.domain.portfolio.service.dto.PortfolioCreateServiceRequest;

@Builder
public record PortfolioCreateRequest(
    @NotBlank(message = "포트폴리오 제목은 필수입니다.")
    String title,
    @NotBlank(message = "포트폴리오 내용은 필수입니다.")
    String content
) {
    public PortfolioCreateServiceRequest toServiceRequest() {
        return PortfolioCreateServiceRequest.builder()
            .title(title)
            .content(content)
            .build();
    }
}

package soon.capstone.domain.portfolio.controller.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PortfolioResponse(

    Long id,
    String title,
    String content,
    LocalDateTime createTime,
    LocalDateTime modifyTime

) {
}
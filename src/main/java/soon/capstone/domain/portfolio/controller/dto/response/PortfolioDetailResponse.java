package soon.capstone.domain.portfolio.controller.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PortfolioDetailResponse(

    Long id,
    String title,
    String content,
    String nickname,
    String profileImageURL,
    LocalDateTime createTime,
    LocalDateTime modifyTime

) {
}
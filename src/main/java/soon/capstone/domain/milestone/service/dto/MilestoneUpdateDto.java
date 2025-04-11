package soon.capstone.domain.milestone.service.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MilestoneUpdateDto(
        String owner,
        String repo,
        String oauthToken,
        String title,
        String description,
        LocalDateTime dueDate,
        LocalDateTime startDate
) {
    public static MilestoneUpdateDto of(
            String owner,
            String repo,
            String oauthToken,
            String title,
            String description,
            LocalDateTime dueDate,
            LocalDateTime startDate
    ) {
        return MilestoneUpdateDto.builder()
                .owner(owner)
                .repo(repo)
                .oauthToken(oauthToken)
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .startDate(startDate)
                .build();
    }
}

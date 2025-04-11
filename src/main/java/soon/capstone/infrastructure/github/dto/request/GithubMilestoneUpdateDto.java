package soon.capstone.infrastructure.github.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Builder
public record GithubMilestoneUpdateDto(
    String title,
    String state,
    String description,
    @JsonProperty("due_on")
    String dueOn
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public static GithubMilestoneUpdateDto of(String title, String description, LocalDateTime dueOn) {
        return GithubMilestoneUpdateDto.builder()
                .title(title)
                .description(description)
                .dueOn(dueOn != null ? dueOn.atZone(ZoneOffset.UTC).format(FORMATTER) : null)
                .state("open")
                .build();
    }
}
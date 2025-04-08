package soon.capstone.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;

@Builder
public record GithubMilestoneCreationDto(
        String title,
        String description,
        @JsonProperty("due_on")
        String dueOn,
        String state
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public static GithubMilestoneCreationDto of(String title, String description, LocalDateTime dueOn) {
        return GithubMilestoneCreationDto.builder()
                .title(title)
                .description(description)
                .dueOn(dueOn != null ? dueOn.atZone(ZoneOffset.UTC).format(FORMATTER) : null)
                .state("open")
                .build();
    }
}


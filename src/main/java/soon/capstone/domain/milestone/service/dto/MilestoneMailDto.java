package soon.capstone.domain.milestone.service.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record MilestoneMailDto(
    String milestoneTitle,
    String teamName,
    List<String> emails
) {
}

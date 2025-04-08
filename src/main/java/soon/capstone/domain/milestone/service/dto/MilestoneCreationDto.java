package soon.capstone.domain.milestone.service.dto;

import lombok.Builder;
import soon.capstone.domain.project.entity.Project;

import java.time.LocalDateTime;

@Builder
public record MilestoneCreationDto(
        String owner,
        String repo,
        String oauthToken,
        String title,
        String description,
        LocalDateTime dueDate,
        LocalDateTime startDate,
        String creator,
        Project project

) {
    public static MilestoneCreationDto of(
            String owner,
            String repo,
            String oauthToken,
            String title,
            String description,
            LocalDateTime dueDate,
            LocalDateTime startDate,
            String creator,
            Project project
    ) {
        return MilestoneCreationDto.builder()
                .owner(owner)
                .repo(repo)
                .oauthToken(oauthToken)
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .startDate(startDate)
                .creator(creator)
                .project(project)
                .build();
    }
}

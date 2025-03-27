package soon.capstone.infrastructure.github.dto;

import lombok.Builder;

@Builder
public record GithubRepositoryCreationDto(
        String name,
        String description,
        String homepage,
        boolean _private,
        boolean hasIssues,
        boolean hasProjects,
        boolean hasWiki
) {
    public static GithubRepositoryCreationDto of(String repoName) {
        return GithubRepositoryCreationDto.builder()
                .name(repoName)
                .description("Temporarily created repository")
                .homepage(null)
                ._private(false)
                .hasIssues(true)
                .hasProjects(true)
                .hasWiki(true)
                .build();
    }
}

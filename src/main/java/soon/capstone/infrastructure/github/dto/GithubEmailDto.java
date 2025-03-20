package soon.capstone.infrastructure.github.dto;

public record GithubEmailDto(

    String email,
    boolean primary,
    boolean verified

) {
}
package soon.capstone.external.github.dto;

public record GithubEmailDto(

    String email,
    boolean primary,
    boolean verified

) {
}
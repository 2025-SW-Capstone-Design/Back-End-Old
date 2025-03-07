package soon.capstone.external.github.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import soon.capstone.external.config.RestClientConfig;
import soon.capstone.external.github.dto.GithubEmailDto;
import soon.capstone.global.exception.github.GithubMemberEmailNotFoundException;
import soon.capstone.global.exception.github.PrimaryVerifiedEmailNotFoundException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GithubEmailService {

    private static final String EMAILS_URL = "/user/emails";
    private final RestClientConfig restClientConfig;

    public String fetchPrimaryVerifiedEmail(String oauth2Token) {
        List<GithubEmailDto> emails = fetchEmails(oauth2Token);
        return extractPrimaryVerifiedEmail(emails);
    }

    private List<GithubEmailDto> fetchEmails(String oauth2Token) {
        RestClient restClient = restClientConfig.githubRestClient(oauth2Token);
        return Optional.ofNullable(
            restClient.get()
                .uri(EMAILS_URL)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GithubEmailDto>>() {
                })
        ).orElseThrow(GithubMemberEmailNotFoundException::new);
    }

    private String extractPrimaryVerifiedEmail(List<GithubEmailDto> emails) {
        return emails.stream()
            .filter(email -> email.primary() && email.verified())
            .map(GithubEmailDto::email)
            .findFirst()
            .orElseThrow(PrimaryVerifiedEmailNotFoundException::new);
    }

}
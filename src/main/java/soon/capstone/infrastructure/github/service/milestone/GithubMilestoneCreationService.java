package soon.capstone.infrastructure.github.service.milestone;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.infrastructure.github.dto.GithubMilestoneCreationDto;
import soon.capstone.infrastructure.restclient.config.RestClientConfig;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class GithubMilestoneCreationService {

    private static final String MILESTONE_CREATION_URL = "/repos/{owner}/{repo}/milestones";
    private final RestClientConfig restClientConfig;

    public void createMilestone(String owner, String repo, String oauthToken, String title, String description, LocalDateTime dueDate) {
        String url = MILESTONE_CREATION_URL
                .replace("{owner}", owner)
                .replace("{repo}", repo);

        restClientConfig.githubRestClient(oauthToken)
                .post()
                .uri(url)
                .body(GithubMilestoneCreationDto.of(title, description, dueDate))
                .retrieve()
                .body(Void.class);
    }

}

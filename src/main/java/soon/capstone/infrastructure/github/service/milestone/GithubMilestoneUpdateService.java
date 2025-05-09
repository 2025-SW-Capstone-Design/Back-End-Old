package soon.capstone.infrastructure.github.service.milestone;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.infrastructure.github.dto.request.GithubMilestoneUpdateDto;
import soon.capstone.infrastructure.restclient.config.RestClientConfig;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class GithubMilestoneUpdateService {

    private static final String MILESTONE_UPDATE_URL = "/repos/{owner}/{repo}/milestones/{milestone_number}";
    private final RestClientConfig restClientConfig;

    public void updateMilestone(String owner, String repo, int milestoneNumber, String oauthToken, String title, String description, LocalDateTime dueDate, String status) {

        String url = MILESTONE_UPDATE_URL
                .replace("{owner}", owner)
                .replace("{repo}", repo)
                .replace("{milestone_number}", String.valueOf(milestoneNumber));

        GithubMilestoneUpdateDto updateDto = GithubMilestoneUpdateDto.of(title, description, dueDate, status);

        restClientConfig.githubRestClient(oauthToken)
                .patch()
                .uri(url)
                .body(updateDto)
                .retrieve()
                .body(Void.class);
    }
}

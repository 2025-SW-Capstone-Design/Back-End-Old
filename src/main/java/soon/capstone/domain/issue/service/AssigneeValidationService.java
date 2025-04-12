package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import soon.capstone.infrastructure.github.service.dto.GithubAssigneesServiceRequest;
import soon.capstone.infrastructure.github.service.issue.GithubAssigneesService;

@RequiredArgsConstructor
@Service
public class AssigneeValidationService {

    private final GithubAssigneesService githubAssigneesService;

    @Cacheable(value = "assignee", key = "#memberId + #organizationName + #repositoryName + #assignees")
    public boolean isAssigneeValid(
        Long memberId,
        String organizationName,
        String repositoryName,
        String assignees
    ) {
        GithubAssigneesServiceRequest request = GithubAssigneesServiceRequest.builder()
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .assignee(assignees)
            .memberId(memberId)
            .build();
        return githubAssigneesService.isAssignee(request);
    }
}
package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import soon.capstone.infrastructure.github.service.dto.GithubAssigneesAppendServiceRequest;
import soon.capstone.infrastructure.github.service.dto.GithubAssigneesServiceRequest;
import soon.capstone.infrastructure.github.service.issue.GithubAssigneesService;

@RequiredArgsConstructor
@Service
public class AssigneeService {

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

    @CacheEvict(value = "assignee", key = "#memberId + #organizationName + #repositoryName + #assignees")
    public void appendIssueWithAssignee(
        Long memberId,
        Long issueNumber,
        String organizationName,
        String repositoryName,
        String assignees
    ) {
        GithubAssigneesAppendServiceRequest request = GithubAssigneesAppendServiceRequest.builder()
            .memberId(memberId)
            .issueNumber(issueNumber)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .assignee(assignees)
            .build();
        githubAssigneesService.appendIssueWithAssignee(request);
    }

}
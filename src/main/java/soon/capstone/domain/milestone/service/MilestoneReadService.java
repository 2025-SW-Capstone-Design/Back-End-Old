package soon.capstone.domain.milestone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.issue.repository.issue.IssueRepository;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.milestone.service.dto.response.MilestoneDetailResponse;
import soon.capstone.domain.milestone.service.dto.response.MilestoneIssueResponse;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.team.entity.Team;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MilestoneReadService {

    private final MilestoneRepository milestoneRepository;
    private final IssueRepository issueRepository;

    public List<MilestoneResponse> getMilestonesByProject(Project project) {
        return milestoneRepository.getMilestonesByProject(project);
    }

    public List<MilestoneResponse> getMilestonesByTeam(Team team) {
        return milestoneRepository.getMilestonesByTeam(team);
    }

    @Transactional(readOnly = true)
    public MilestoneDetailResponse getMilestoneDetail(Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId);
        List<IssueDetailResponse> issueDetailResponses = issueRepository.findIssuesWithLabelsByMilestoneId(milestoneId);

        return MilestoneDetailResponse.builder()
            .milestoneId(milestone.getId())
            .creator(milestone.getCreator())
            .title(milestone.getTitle())
            .description(milestone.getDescription())
            .startDate(milestone.getStartDate())
            .dueDate(milestone.getDueDate())
            .isCompleted(milestone.isCompleted())
            .issues(issueDetailResponses)
            .build();
    }

    @Transactional(readOnly = true)
    public List<MilestoneIssueResponse> getMilestoneWithIssuesDueTomorrow() {
        List<MilestoneResponse> milestones = milestoneRepository.getMilestoneWithIssuesDueTomorrow();

        return milestones.stream()
            .map(milestone -> MilestoneIssueResponse.builder()
                .milestone(milestone)
                .issues(issueRepository.findIssuesWithLabelsByMilestoneId(milestone.milestoneId()))
                .build())
            .toList();
    }

}
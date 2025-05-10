package soon.capstone.domain.milestone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.milestone.entity.MilestoneStatus;
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.milestone.service.dto.MilestoneCreationDto;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.global.exception.milestone.MilestoneDuplicateTitleException;
import soon.capstone.global.exception.milestone.MilestoneInvalidDateException;
import soon.capstone.infrastructure.github.service.milestone.GithubMilestoneCreationService;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class MilestoneCreationService {

    private final GithubMilestoneCreationService githubMilestoneCreationService;
    private final MilestoneRepository milestoneRepository;

    public Long createMilestone(MilestoneCreationDto milestoneCreationDto) {

        validateMilestoneDates(milestoneCreationDto.startDate(), milestoneCreationDto.dueDate());
        isDuplicateMilestoneTitle(milestoneCreationDto.title(), milestoneCreationDto.project());

        int githubMilestoneId = githubMilestoneCreationService.createMilestone(
            milestoneCreationDto.owner(),
            milestoneCreationDto.repo(),
            milestoneCreationDto.oauthToken(),
            milestoneCreationDto.title(),
            milestoneCreationDto.description(),
            milestoneCreationDto.dueDate()
        );

        Milestone milestone = createMilestoneEntity(
            milestoneCreationDto.title(),
            milestoneCreationDto.description(),
            milestoneCreationDto.creator(),
            milestoneCreationDto.dueDate(),
            milestoneCreationDto.startDate(),
            setMilestoneStatus(
                milestoneCreationDto.startDate(),
                milestoneCreationDto.dueDate()
            ),
            githubMilestoneId,
            milestoneCreationDto.project()
        );

        milestoneRepository.save(milestone);

        return milestone.getId();
    }

    private void validateMilestoneDates(LocalDateTime startDate, LocalDateTime dueDate) {
        if (startDate != null && dueDate != null && startDate.isAfter(dueDate)) {
            throw new MilestoneInvalidDateException();
        }
    }

    private void isDuplicateMilestoneTitle(String title, Project project) {
        if (milestoneRepository.existsByTitleAndProject(title, project)) {
            throw new MilestoneDuplicateTitleException();
        }
    }

    private MilestoneStatus setMilestoneStatus(LocalDateTime startDate, LocalDateTime dueDate) {
        return startDate.isAfter(LocalDateTime.now()) && dueDate.isAfter(startDate) ? MilestoneStatus.NOT_STARTED : MilestoneStatus.IN_PROGRESS;
    }

    private Milestone createMilestoneEntity(String title, String description, String creator, LocalDateTime dueDate, LocalDateTime startDate, MilestoneStatus status, int githubMilestoneId, Project project) {
        return Milestone.builder()
            .title(title)
            .description(description)
            .dueDate(dueDate)
            .creator(creator)
            .startDate(startDate)
            .status(status)
            .githubMilestoneId(githubMilestoneId)
            .project(project)
            .build();
    }
}

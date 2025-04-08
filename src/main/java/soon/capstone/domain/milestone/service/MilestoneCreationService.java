package soon.capstone.domain.milestone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.milestone.service.dto.MilestoneCreationDto;
import soon.capstone.domain.project.entity.Project;
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

        githubMilestoneCreationService.createMilestone(
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

    private Milestone createMilestoneEntity(String title, String description, String creator, LocalDateTime dueDate, LocalDateTime startDate, Project project) {
        return Milestone.builder()
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .creator(creator)
                .startDate(startDate)
                .project(project)
                .build();
    }
}

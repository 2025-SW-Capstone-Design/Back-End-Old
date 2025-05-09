package soon.capstone.domain.milestone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.milestone.service.dto.MilestoneUpdateDto;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.global.exception.milestone.MilestoneInvalidDateException;
import soon.capstone.infrastructure.github.service.milestone.GithubMilestoneUpdateService;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class MilestoneUpdateService {

    private final MilestoneRepository milestoneRepository;
    private final GithubMilestoneUpdateService githubMilestoneUpdateService;

    @Transactional
    public MilestoneResponse updateMilestone(Long milestoneId, MilestoneUpdateDto request) {
        Milestone milestone = milestoneRepository.findById(milestoneId);

        String updatedTitle = request.title() != null ? request.title() : milestone.getTitle();
        String updatedDescription = request.description() != null ? request.description() : milestone.getDescription();
        LocalDateTime updatedDueDate = request.dueDate() != null ? request.dueDate() : milestone.getDueDate();
        LocalDateTime updatedStartDate = request.startDate() != null ? request.startDate() : milestone.getStartDate();

        validateMilestoneDates(updatedStartDate, updatedDueDate);

        milestone.updateMilestone(
            updatedTitle,
            updatedDescription,
            updatedDueDate,
            updatedStartDate
        );

        githubMilestoneUpdateService.updateMilestone(
            request.owner(),
            request.repo(),
            milestone.getGithubMilestoneId(),
            request.oauthToken(),
            updatedTitle,
            updatedDescription,
            updatedDueDate,
            milestone.getStatus().name() // TODO: 이넘 활용으로 변경
        );

        return MilestoneResponse.builder()
            .milestoneId(milestone.getId())
            .title(milestone.getTitle())
            .description(milestone.getDescription())
            .creator(milestone.getCreator())
            .dueDate(milestone.getDueDate())
            .startDate(milestone.getStartDate())
            .status(milestone.getStatus().name())
            .build();
    }

    private void validateMilestoneDates(LocalDateTime startDate, LocalDateTime dueDate) {
        if (startDate != null && dueDate != null && startDate.isAfter(dueDate)) {
            throw new MilestoneInvalidDateException();
        }
    }

}

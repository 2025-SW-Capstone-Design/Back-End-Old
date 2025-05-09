package soon.capstone.domain.milestone.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.milestone.service.dto.MilestoneMailDto;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.global.exception.milestone.MilestoneNotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class MilestoneRepository {

    private final MilestoneJpaRepository milestoneJpaRepository;

    public void save(Milestone milestone) {
        milestoneJpaRepository.save(milestone);
    }

    public void saveAll(List<Milestone> milestones) {
        milestoneJpaRepository.saveAll(milestones);
    }

    public Milestone findById(Long milestoneId) {
        return milestoneJpaRepository.findById(milestoneId)
            .orElseThrow(MilestoneNotFoundException::new);
    }

    public void deleteAllInBatch() {
        milestoneJpaRepository.deleteAllInBatch();
    }

    public List<MilestoneResponse> getMilestonesByProject(Project project) {
        return milestoneJpaRepository.getMilestonesByProject(project);
    }

    public List<MilestoneResponse> getMilestonesByTeam(Team team) {
        return milestoneJpaRepository.getMilestonesByTeam(team);
    }

    public List<MilestoneResponse> getMilestoneWithIssuesDueTomorrow(Long teamId) {
        return milestoneJpaRepository.getMilestoneWithIssuesDueTomorrow(teamId);
    }

    public boolean existsByTitle(String title) {
        return milestoneJpaRepository.existsByTitle(title);
    }

    public List<Milestone> findAll() {
        return milestoneJpaRepository.findAll();
    }

    public List<MilestoneMailDto> getEmailsByMilestones() {
        return milestoneJpaRepository.getEmailsByMilestones();
    }
}
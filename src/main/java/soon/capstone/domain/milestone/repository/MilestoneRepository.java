package soon.capstone.domain.milestone.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.global.exception.milestone.MilestoneNotFoundException;

@RequiredArgsConstructor
@Repository
public class MilestoneRepository {

    private final MilestoneJpaRepository milestoneJpaRepository;

    public void save(Milestone milestone) {
        milestoneJpaRepository.save(milestone);
    }

    public Milestone findById(Long milestoneId) {
        return milestoneJpaRepository.findById(milestoneId)
                .orElseThrow(MilestoneNotFoundException::new);
    }

    public void deleteAllInBatch() {
        milestoneJpaRepository.deleteAllInBatch();
    }
}
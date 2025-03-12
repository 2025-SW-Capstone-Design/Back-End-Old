package soon.capstone.domain.teammember.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.global.exception.teammember.TeamMemberNotFoundException;

@RequiredArgsConstructor
@Repository
public class TeamMemberRepository {

    private final TeamMemberJpaRepository teamMemberJpaRepository;

    public void save(TeamMember teamMember) {
        teamMemberJpaRepository.save(teamMember);
    }

    public TeamMember findById(Long teamMemberId) {
        return teamMemberJpaRepository.findById(teamMemberId)
            .orElseThrow(TeamMemberNotFoundException::new);
    }

    public void deleteAllInBatch() {
        teamMemberJpaRepository.deleteAllInBatch();
    }

}
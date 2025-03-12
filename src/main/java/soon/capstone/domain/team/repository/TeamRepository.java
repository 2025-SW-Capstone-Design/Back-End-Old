package soon.capstone.domain.team.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.global.exception.team.TeamNotFoundException;

@RequiredArgsConstructor
@Repository
public class TeamRepository {

    private final TeamJpaRepository teamJpaRepository;

    public void save(Team team) {
        teamJpaRepository.save(team);
    }

    public Team findById(Long teamId) {
        return teamJpaRepository.findById(teamId)
            .orElseThrow(TeamNotFoundException::new);
    }

    public void deleteAllInBatch() {
        teamJpaRepository.deleteAllInBatch();
    }

    public boolean existsByNameOrOrganizationName(String name, String organizationName) {
        return teamJpaRepository.existsByNameOrOrganizationName(name, organizationName);
    }

}
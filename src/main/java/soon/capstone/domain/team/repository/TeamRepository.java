package soon.capstone.domain.team.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TeamRepository {

    private final TeamJpaRepository teamJpaRepository;

}
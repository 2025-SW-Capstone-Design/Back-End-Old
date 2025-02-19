package soon.capstone.domain.project.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProjectRepository {

    private final ProjectJpaRepository projectJpaRepository;

}
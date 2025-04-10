package soon.capstone.domain.readme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.readme.entity.Readme;

import java.util.Optional;

public interface ReadmeJpaRepository extends JpaRepository<Readme, Long> {

    Optional<Readme> findByProjectIdAndIsLatestTrue(Long projectId);

}
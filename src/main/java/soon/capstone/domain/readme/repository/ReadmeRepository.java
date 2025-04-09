package soon.capstone.domain.readme.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReadmeRepository {

    private final ReadmeJpaRepository readmeJpaRepository;

}
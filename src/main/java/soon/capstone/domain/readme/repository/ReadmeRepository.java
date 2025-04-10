package soon.capstone.domain.readme.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.readme.entity.Readme;
import soon.capstone.global.exception.readme.ReadmeNotFoundException;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ReadmeRepository {

    private final ReadmeJpaRepository readmeJpaRepository;

    public Long save(Readme readme) {
        return readmeJpaRepository.save(readme)
            .getId();
    }

    public Readme findById(Long id) {
        return readmeJpaRepository.findById(id)
            .orElseThrow(ReadmeNotFoundException::new);
    }

    public Optional<Readme> findByProjectIdAndLatestIsTrue(Long projectId) {
        return readmeJpaRepository.findByProjectIdAndIsLatestTrue(projectId);
    }

    public void deleteAllInBatch() {
        readmeJpaRepository.deleteAllInBatch();
    }

    public void delete(Readme readme) {
        readmeJpaRepository.delete(readme);
    }

}
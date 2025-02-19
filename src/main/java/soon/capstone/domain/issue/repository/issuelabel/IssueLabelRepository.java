package soon.capstone.domain.issue.repository.issuelabel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class IssueLabelRepository {

    private final IssueLabelJpaRepository issueLabelJpaRepository;

}
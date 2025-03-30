package soon.capstone.domain.issue.repository.issuelabel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class IssueLabelRelationRepository {

    private final IssueLabelRelationJpaRepository issueLabelRelationJpaRepository;

}
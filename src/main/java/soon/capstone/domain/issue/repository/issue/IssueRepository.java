package soon.capstone.domain.issue.repository.issue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class IssueRepository {

    private final IssueJpaRepository issueJpaRepository;

}
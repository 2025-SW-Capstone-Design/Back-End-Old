package soon.capstone.domain.issue.repository.issue;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.issue.entity.Issue;

import java.util.List;

public interface IssueJpaRepository extends JpaRepository<Issue, Long>, IssueListRepository {

    List<Issue> findByGithubIssueNumberIn(List<Long> githubIssueNumbers);

}
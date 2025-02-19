package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.issue.repository.issue.IssueRepository;

@RequiredArgsConstructor
@Service
public class IssueService {

    private final IssueRepository issueRepository;

}
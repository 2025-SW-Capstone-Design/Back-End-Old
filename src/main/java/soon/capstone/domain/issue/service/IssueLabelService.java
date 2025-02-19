package soon.capstone.domain.issue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.issue.repository.issuelabel.IssueLabelRepository;

@RequiredArgsConstructor
@Service
public class IssueLabelService {

    private final IssueLabelRepository issueLabelRepository;

}
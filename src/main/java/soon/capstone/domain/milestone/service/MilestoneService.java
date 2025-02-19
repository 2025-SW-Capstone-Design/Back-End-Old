package soon.capstone.domain.milestone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.milestone.repository.MilestoneRepository;

@RequiredArgsConstructor
@Service
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;

}
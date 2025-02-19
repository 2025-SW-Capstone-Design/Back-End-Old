package soon.capstone.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.project.repository.ProjectRepository;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

}
package soon.capstone.domain.readme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.readme.repository.ReadmeRepository;

@RequiredArgsConstructor
@Service
public class ReadmeService {

    private final ReadmeRepository readmeRepository;

}
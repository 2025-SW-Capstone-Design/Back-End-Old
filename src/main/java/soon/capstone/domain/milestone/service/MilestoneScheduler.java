package soon.capstone.domain.milestone.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.milestone.service.dto.MilestoneMailDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class MilestoneScheduler {

    private final MilestoneRepository milestoneRepository;
    private final MilestoneMailService milestoneMailService;

    @Scheduled(cron = "0 0 0 * * *")
    public void sendMilestoneEmail() {
        List<MilestoneMailDto> mailList = milestoneRepository.getEmailsByMilestones();

        mailList.forEach(milestoneMailDto -> {
            List<String> emails = milestoneMailDto.emails();
            String milestoneTitle = milestoneMailDto.milestoneTitle();
            String teamName = milestoneMailDto.teamName();

            milestoneMailService.sendMilestoneEmail(emails, milestoneTitle, teamName);
            log.info("Milestone email sent to {} for milestone {} of team {}", emails, milestoneTitle, teamName);
        });
    }
}

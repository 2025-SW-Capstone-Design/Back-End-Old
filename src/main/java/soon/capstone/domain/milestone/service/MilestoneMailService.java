package soon.capstone.domain.milestone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.global.email.service.EmailSendService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MilestoneMailService {

    private final EmailSendService emailSendService;

    public void sendMilestoneEmail(List<String> emails, String milestoneTitle, String teamName) {
        emails.forEach(email -> emailSendService.sendMilestoneEmail(email, milestoneTitle, teamName));
    }
}

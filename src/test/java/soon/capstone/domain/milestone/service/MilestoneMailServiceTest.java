package soon.capstone.domain.milestone.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.email.service.EmailSendService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class MilestoneMailServiceTest extends IntegrationTestSupport {

    @Autowired
    private MilestoneMailService milestoneMailService;

    @MockitoBean
    private EmailSendService emailSendService;

    @DisplayName("마감일이 하루 남은 마일스톤에 대한 이메일을 전송한다.")
    @Test
    void sendMilestoneEmail() {
        // Given
        String milestoneTitle = "Test Milestone";
        String teamName = "Test Team";
        List<String> emails = List.of("test1@example.com", "test2@example.com");

        // When
        milestoneMailService.sendMilestoneEmail(emails, milestoneTitle, teamName);

        // Then
        then(emailSendService)
            .should(times(2))
            .sendMilestoneEmail(anyString(), eq(milestoneTitle), eq(teamName));
    }
}
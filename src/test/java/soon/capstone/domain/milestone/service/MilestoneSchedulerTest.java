package soon.capstone.domain.milestone.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.milestone.repository.MilestoneRepository;
import soon.capstone.domain.milestone.service.dto.MilestoneMailDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class MilestoneSchedulerTest extends IntegrationTestSupport {

    @Autowired
    private MilestoneScheduler milestoneScheduler;

    @Autowired
    private ScheduledTaskHolder scheduledTaskHolder;

    @MockitoBean
    private MilestoneRepository milestoneRepository;

    @MockitoBean
    private MilestoneMailService milestoneMailService;

    @DisplayName("여러 마일스톤이 있을 때 이메일이 각각 발송되어야 한다")
    @Test
    void sendMilestoneEmail_WithMultipleMilestones_ShouldSendEmails() {
        // Given
        MilestoneMailDto milestone1 = new MilestoneMailDto(
            "마일스톤 1",
            "팀 A",
            List.of("user1@example.com", "user2@example.com")
        );

        MilestoneMailDto milestone2 = new MilestoneMailDto(
            "마일스톤 2",
            "팀 B",
            List.of("user3@example.com")
        );

        List<MilestoneMailDto> mailList = Arrays.asList(milestone1, milestone2);

        given(milestoneRepository.getEmailsByMilestones()).willReturn(mailList);

        // When
        milestoneScheduler.sendMilestoneEmail();

        // Then
        verify(milestoneMailService, times(1))
            .sendMilestoneEmail(milestone1.emails(), milestone1.milestoneTitle(), milestone1.teamName());
        verify(milestoneMailService, times(1))
            .sendMilestoneEmail(milestone2.emails(), milestone2.milestoneTitle(), milestone2.teamName());
    }

    @DisplayName("마감일이 하루 남은 마일스톤이 없을 때, 이메일이 발송되지 않아야 한다")
    @Test
    void sendMilestoneEmail_WithNoMilestones_ShouldNotSendEmails() {
        // Given
        given(milestoneRepository.getEmailsByMilestones()).willReturn(Collections.emptyList());

        // When
        milestoneScheduler.sendMilestoneEmail();

        // Then
        verify(milestoneMailService, never()).sendMilestoneEmail(anyList(), anyString(), anyString());
    }

    @DisplayName("스케줄러가 매일 자정에 실행되도록 설정되어 있어야 한다")
    @Test
    void verifyScheduledAnnotation_ShouldRunAtMidnightDaily() throws NoSuchMethodException {
        // Given
        Set<ScheduledTask> scheduledTasks = scheduledTaskHolder.getScheduledTasks();
        String expectedCronExpression = "0 0 0 * * *";

        // When
        boolean found = scheduledTasks.stream()
            .filter(task -> task.getTask() instanceof CronTask)
            .map(task -> (CronTask) task.getTask())
            .anyMatch(cronTask -> cronTask.getExpression().equals(expectedCronExpression));

        // Then
        assertThat(scheduledTasks).as("예약된 작업이 존재해야 합니다").isNotEmpty();
        assertThat(found).as("매일 자정에 실행되는 크론 작업이 존재해야 합니다").isTrue();
    }

    @DisplayName("크론 표현식이 자정에 정확히 실행되도록 설정되어 있어야 한다")
    @Test
    void testCronExpressionTiming() {
        // Given
        String cronExpression = "0 0 0 * * *";
        CronExpression cron = CronExpression.parse(cronExpression);

        // When & Then
        LocalDateTime currentTime1 = LocalDateTime.of(2025, 4, 17, 23, 59, 59);
        LocalDateTime currentTime2 = LocalDateTime.of(2025, 4, 17, 0, 0, 1);

        LocalDateTime nextRun1 = cron.next(currentTime1);
        LocalDateTime nextRun2 = cron.next(currentTime2);

        LocalDateTime expected1 = LocalDateTime.of(2025, 4, 18, 0, 0, 0);
        LocalDateTime expected2 = LocalDateTime.of(2025, 4, 18, 0, 0, 0);

        // Then
        assertThat(expected1).isEqualTo(nextRun1);
        assertThat(expected2).isEqualTo(nextRun2);
    }

}
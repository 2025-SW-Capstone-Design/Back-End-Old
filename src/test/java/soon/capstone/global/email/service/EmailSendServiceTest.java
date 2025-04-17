package soon.capstone.global.email.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mail.javamail.JavaMailSender;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.global.exception.common.EmailSendException;
import soon.capstone.global.exception.dto.ErrorDetail;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class EmailSendServiceTest extends IntegrationTestSupport {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailSendService emailSendService;

    @DisplayName("초대 코드가 포함된 이메일을 전송한다.")
    @Test
    void sendInvitationCodeEmail() {
        // given
        String email = "test@example.com";
        String invitationCode = "ABCD1234";
        MimeMessage mimeMessage = mock(MimeMessage.class);
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);

        // expected
        assertThatCode(() -> emailSendService.sendInvitationCodeEmail(email, invitationCode))
            .doesNotThrowAnyException();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @DisplayName("마감일이 하루 남은 마일스톤에 대하여 이메일을 전송한다.")
    @Test
    void sendMilestoneEmail() {
        // given
        String email = "test@example.com";
        String milestoneTitle = "마일스톤 제목";
        String teamName = "팀 이름";
        MimeMessage mimeMessage = mock(MimeMessage.class);
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);

        // expected
        assertThatCode(() -> emailSendService.sendMilestoneEmail(email, milestoneTitle, teamName))
            .doesNotThrowAnyException();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @DisplayName("이메일을 전송 하는 중 실패한다면 예외가 발생한다")
    @Test
    void sendInvitationCodeEmailException() {
        // given
        String email = "test@example.com";
        String invitationCode = "ABCD1234";

        given(mailSender.createMimeMessage()) // TODO: 고민 후 수정 필요
            .willThrow(new EmailSendException());

        // expected
        assertThatThrownBy(() -> emailSendService.sendInvitationCodeEmail(email, invitationCode))
            .isInstanceOf(EmailSendException.class)
            .hasMessage(ErrorDetail.EMAIL_SEND.getMessage());
    }

}
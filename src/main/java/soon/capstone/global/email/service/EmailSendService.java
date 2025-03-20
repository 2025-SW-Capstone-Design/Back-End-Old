package soon.capstone.global.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import soon.capstone.global.exception.common.EmailSendException;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailSendService {

    private final JavaMailSender mailSender;

    private static final String INVITATION_CODE_SUBJECT = "초대 코드 발급 안내";

    public void sendInvitationCodeEmail(String email, String invitationCode) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(email);
            helper.setSubject(INVITATION_CODE_SUBJECT);
            helper.setText(invitationCode);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("이메일 전송 중 에러 발생 : {}", email, e);
            throw new EmailSendException();
        }
    }

}
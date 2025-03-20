package soon.capstone.domain.team.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.global.email.service.EmailSendService;
import soon.capstone.infrastructure.redis.invitation.repository.InvitationCodeRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TeamInvitationService {

    private final InvitationCodeGenerator invitationCodeGenerator;
    private final InvitationCodeRepository invitationCodeRepository;
    private final EmailSendService emailSendService;

    public String generateInvitationCode(Long teamId) {
        return invitationCodeGenerator.generateInvitationCode(teamId);
    }

    @Transactional(readOnly = true)
    public void sendInvitationEmails(Long teamId, List<String> emails) {
        String invitationCode = invitationCodeRepository.findByTeamId(teamId).getCode();
        emails.forEach(email -> emailSendService.sendInvitationCodeEmail(email, invitationCode));
    }

}
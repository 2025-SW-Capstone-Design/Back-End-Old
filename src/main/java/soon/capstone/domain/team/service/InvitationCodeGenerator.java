package soon.capstone.domain.team.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import soon.capstone.infrastructure.redis.invitation.entity.InvitationCode;
import soon.capstone.infrastructure.redis.invitation.repository.InvitationCodeRepository;

@RequiredArgsConstructor
@Component
public class InvitationCodeGenerator {

    private static final int INVITATION_CODE_LENGTH = 8;
    private final InvitationCodeRepository invitationCodeRepository;

    public String generateInvitationCode(Long teamId) {
        String code = RandomStringUtils.randomAlphanumeric(INVITATION_CODE_LENGTH);
        InvitationCode invitationCode = InvitationCode.builder()
            .code(code)
            .teamId(teamId)
            .build();
        invitationCodeRepository.save(invitationCode);

        return code;
    }

}
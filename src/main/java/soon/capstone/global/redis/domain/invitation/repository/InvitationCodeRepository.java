package soon.capstone.global.redis.domain.invitation.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.global.redis.domain.invitation.entity.InvitationCode;

@RequiredArgsConstructor
@Repository
public class InvitationCodeRepository {

    private final InvitationCodeRedisRepository invitationCodeRedisRepository;

    public void save(InvitationCode invitationCode) {
        invitationCodeRedisRepository.save(invitationCode);
    }

}
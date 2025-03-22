package soon.capstone.infrastructure.redis.invitation.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.global.exception.team.InvitationCodeNotFoundException;
import soon.capstone.infrastructure.redis.invitation.entity.InvitationCode;

@RequiredArgsConstructor
@Repository
public class InvitationCodeRepository {

    private final InvitationCodeRedisRepository invitationCodeRedisRepository;

    public void save(InvitationCode invitationCode) {
        invitationCodeRedisRepository.save(invitationCode);
    }

    public InvitationCode findByTeamId(Long teamId) {
        return invitationCodeRedisRepository.findByTeamId(teamId)
            .orElseThrow(InvitationCodeNotFoundException::new);
    }

    public boolean existsByTeamId(Long teamId) {
        return invitationCodeRedisRepository.existsByTeamId(teamId);
    }

    public void deleteAll() {
        invitationCodeRedisRepository.deleteAll();
    }

    public InvitationCode findByCode(String code) {
        return invitationCodeRedisRepository.findByCode(code)
            .orElseThrow(InvitationCodeNotFoundException::new);
    }

}
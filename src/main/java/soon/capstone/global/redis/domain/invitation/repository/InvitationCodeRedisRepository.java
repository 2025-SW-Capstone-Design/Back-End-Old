package soon.capstone.global.redis.domain.invitation.repository;

import org.springframework.data.repository.CrudRepository;
import soon.capstone.global.redis.domain.invitation.entity.InvitationCode;

import java.util.Optional;

public interface InvitationCodeRedisRepository extends CrudRepository<InvitationCode, Long> {

    boolean existsByTeamId(Long teamId);

    Optional<InvitationCode> findByTeamId(Long teamId);

    void deleteByTeamId(Long teamId);

}
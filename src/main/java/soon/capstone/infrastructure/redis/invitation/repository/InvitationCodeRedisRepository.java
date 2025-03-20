package soon.capstone.infrastructure.redis.invitation.repository;

import org.springframework.data.repository.CrudRepository;
import soon.capstone.infrastructure.redis.invitation.entity.InvitationCode;

import java.util.Optional;

public interface InvitationCodeRedisRepository extends CrudRepository<InvitationCode, Long> {

    boolean existsByTeamId(Long teamId);

    Optional<InvitationCode> findByTeamId(Long teamId);

    void deleteByTeamId(Long teamId);

}
package soon.capstone.infrastructure.redis.openvidu.repository;

import org.springframework.data.repository.CrudRepository;
import soon.capstone.infrastructure.redis.openvidu.entity.TemporaryRoomIdentity;

public interface TemporaryRoomIdentityRedisRepository extends CrudRepository<TemporaryRoomIdentity, String> {
}
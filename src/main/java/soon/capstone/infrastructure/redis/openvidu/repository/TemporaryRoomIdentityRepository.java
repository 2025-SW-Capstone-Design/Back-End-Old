package soon.capstone.infrastructure.redis.openvidu.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.global.exception.chatroom.TemporaryRoomIdentityNotFoundException;
import soon.capstone.infrastructure.redis.openvidu.entity.TemporaryRoomIdentity;

@RequiredArgsConstructor
@Repository
public class TemporaryRoomIdentityRepository {

    private final TemporaryRoomIdentityRedisRepository temporaryRoomIdentityRedisRepository;

    public void save(TemporaryRoomIdentity temporaryRoomIdentity) {
        temporaryRoomIdentityRedisRepository.save(temporaryRoomIdentity);
    }

    public TemporaryRoomIdentity findById(String id) {
        return temporaryRoomIdentityRedisRepository.findById(id)
            .orElseThrow(TemporaryRoomIdentityNotFoundException::new);
    }

    public boolean existsById(String id) {
        return temporaryRoomIdentityRedisRepository.existsById(id);
    }

    public void deleteById(String id) {
        temporaryRoomIdentityRedisRepository.deleteById(id);
    }

}
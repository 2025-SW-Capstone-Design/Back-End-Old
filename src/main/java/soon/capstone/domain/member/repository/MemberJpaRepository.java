package soon.capstone.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.member.entity.Member;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByNickname(String nickname);

}
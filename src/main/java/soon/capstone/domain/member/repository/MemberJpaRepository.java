package soon.capstone.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.member.entity.Member;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

}
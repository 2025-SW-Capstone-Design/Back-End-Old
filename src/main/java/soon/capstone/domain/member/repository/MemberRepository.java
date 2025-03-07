package soon.capstone.domain.member.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.global.exception.member.MemberNotFoundException;

@RequiredArgsConstructor
@Repository
public class MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    public void save(Member member) {
        memberJpaRepository.save(member);
    }

    public Member findByNickname(String nickname) {
        return memberJpaRepository.findByNickname(nickname)
            .orElseThrow(MemberNotFoundException::new);
    }

}
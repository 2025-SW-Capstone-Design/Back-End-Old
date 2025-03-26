package soon.capstone.domain.member.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.global.exception.member.MemberNotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    public void save(Member member) {
        memberJpaRepository.save(member);
    }

    public void saveAll(List<Member> members) {
        memberJpaRepository.saveAll(members);
    }

    public Member findByNickname(String nickname) {
        return memberJpaRepository.findByNickname(nickname)
            .orElseThrow(MemberNotFoundException::new);
    }

    public Member findById(Long memberId) {
        return memberJpaRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);
    }

    public void deleteAllInBatch() {
        memberJpaRepository.deleteAllInBatch();
    }

}
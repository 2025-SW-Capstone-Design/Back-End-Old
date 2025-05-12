package soon.capstone.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.member.service.dto.response.MemberDetailResponse;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberDetailResponse getMemberDetail(Long memberId) {
        Member member = memberRepository.findById(memberId);
        return MemberDetailResponse.from(member);
    }

}
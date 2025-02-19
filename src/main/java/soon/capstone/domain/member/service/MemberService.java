package soon.capstone.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.domain.member.repository.MemberRepository;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

}
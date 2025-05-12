package soon.capstone.domain.member.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.member.service.dto.response.MemberDetailResponse;

import static org.assertj.core.api.Assertions.assertThat;

class MemberServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("사용자의 정보를 조회한다.")
    @Test
    void getMemberDetails() {
        // given
        Member member = Member.builder()
            .email("email@email.com")
            .nickname("nickname")
            .profileImageURL("profileImageURL")
            .build();
        memberRepository.save(member);

        // when
        MemberDetailResponse response = memberService.getMemberDetail(member.getId());

        // then
        assertThat(response).isNotNull()
            .extracting("id", "email", "nickname", "profileImageURL")
            .contains(member.getId(), member.getEmail(), member.getNickname(), member.getProfileImageURL());
    }

}
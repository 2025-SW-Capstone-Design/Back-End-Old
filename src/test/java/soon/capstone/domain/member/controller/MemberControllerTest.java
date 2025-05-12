package soon.capstone.domain.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.member.service.dto.response.MemberDetailResponse;
import soon.capstone.global.anootation.TestMember;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/members";

    @TestMember
    @DisplayName("현재 로그인한 멤버의 정보를 조회한다.")
    @Test
    void getMemberDetail() throws Exception {
        // given
        Long memberId = 1L;

        given(memberService.getMemberDetail(memberId))
            .willReturn(MemberDetailResponse.builder()
                .id(memberId)
                .email("email")
                .profileImageURL("profileImageURL")
                .nickname("nickname")
                .build());

        // expected
        mockMvc.perform(
                get(BASE_URL)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(memberId))
            .andExpect(jsonPath("$.email").value("email"))
            .andExpect(jsonPath("$.profileImageURL").value("profileImageURL"))
            .andExpect(jsonPath("$.nickname").value("nickname"));
    }

}
package soon.capstone.domain.chatroom.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;
import soon.capstone.global.anootation.TestMember;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static soon.capstone.domain.teammember.entity.common.Role.ROLE_LEADER;
import static soon.capstone.domain.teammember.entity.common.Role.ROLE_MEMBER;

class ChatRoomTeamMemberControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/teams/{teamId}/chat-rooms/{chatRoomId}/members";

    @TestMember
    @DisplayName("채팅방에 팀 멤버를 추가한다.")
    @Test
    void addTeamMemberToChatRoom() throws Exception {
        // given
        Long teamId = 1L;
        Long chatRoomId = 1L;

        // expected
        mockMvc.perform(
                post(BASE_URL, teamId, chatRoomId)
            )
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @DisplayName("채팅방에 참여한 팀 멤버를 조회한다.")
    @Test
    void getTeamMembersByChatRoom() throws Exception {
        // given
        Long teamId = 1L;
        Long chatRoomId = 1L;

        List<TeamMemberDetailResponse> responses = List.of(
            createTeamMemberDetailResponse(1L, ROLE_LEADER.name()),
            createTeamMemberDetailResponse(2L, ROLE_MEMBER.name())
        );

        given(chatRoomTeamMemberService.getTeamMembersByChatRoom(any()))
            .willReturn(responses);

        // expected
        mockMvc.perform(
                get(BASE_URL, teamId, chatRoomId)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].memberId").value(1))
            .andExpect(jsonPath("$[1].memberId").value(2));
    }

    private TeamMemberDetailResponse createTeamMemberDetailResponse(Long memberId, String role) {
        return TeamMemberDetailResponse.builder()
            .memberId(memberId)
            .position("position")
            .nickname("nickname")
            .profileImageURL("profileimageUrl")
            .role(role)
            .build();
    }

}
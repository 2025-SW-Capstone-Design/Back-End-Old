package soon.capstone.domain.chatroom.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import soon.capstone.ControllerTestSupport;
import soon.capstone.global.anootation.TestMember;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChatRoomTeamMemberControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/{teamId}/chatroom/{chatRoomId}/members";

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

}
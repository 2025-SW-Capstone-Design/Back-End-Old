package soon.capstone.domain.chatroom.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import soon.capstone.ControllerTestSupport;
import soon.capstone.global.anootation.TestMember;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChatRoomControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/teams/{teamId}/chat-rooms";

    @TestMember
    @DisplayName("채팅방을 다시 활성화 한다.")
    @Test
    void resumeRoom() throws Exception {
        // given
        Long teamId = 1L;
        Long chatRoomId = 1L;

        given(chatRoomService.resumeRoom(any()))
            .willReturn(chatRoomId);

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{chatRoomId}", teamId, chatRoomId)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1));
    }

}
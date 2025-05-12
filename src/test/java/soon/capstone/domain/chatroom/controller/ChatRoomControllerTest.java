package soon.capstone.domain.chatroom.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.chatroom.controller.dto.ChatRoomSummarizeRequest;
import soon.capstone.domain.chatroom.service.dto.response.ChatRoomDetailsResponse;
import soon.capstone.global.anootation.TestMember;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @TestMember
    @DisplayName("해당하는 팀의 채팅방 목록을 조회한다.")
    @Test
    void getChatRoomDetails() throws Exception {
        // given
        Long teamId = 1L;

        List<ChatRoomDetailsResponse> response = List.of(
            createChatRoomDetailsResponse(1L),
            createChatRoomDetailsResponse(2L)
        );

        given(chatRoomService.getChatRoomDetails(any()))
            .willReturn(response);

        // expected
        mockMvc.perform(
                get(BASE_URL, teamId)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2));
    }

    @TestMember
    @DisplayName("채팅방 요약을 성공적으로 저장한다")
    @Test
    void summarizeChatroom() throws Exception {
        // given
        Long teamId = 1L;
        Long chatRoomId = 1L;
        String summaryText = "This is a summary.";

        ChatRoomSummarizeRequest request = ChatRoomSummarizeRequest.builder()
            .text(summaryText)
            .isFinal(false)
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/{chatRoomId}/summary", teamId, chatRoomId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    private ChatRoomDetailsResponse createChatRoomDetailsResponse(Long chatRoomId) {
        return ChatRoomDetailsResponse.builder()
            .id(chatRoomId)
            .title("title")
            .active(true)
            .reservedAt(LocalDateTime.now())
            .build();
    }

}
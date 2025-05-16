package soon.capstone.domain.meetinglog.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.meetinglog.controller.dto.MeetingLogUpdateRequest;
import soon.capstone.global.anootation.TestMember;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MeetingLogControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/{teamId}/meeting-logs";

    @TestMember
    @DisplayName("회의록을 수정한다")
    @Test
    void update() throws Exception {
        // given
        var request = MeetingLogUpdateRequest.builder()
            .title("title")
            .content("content")
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/{meetingLogId}", 1L, 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

}
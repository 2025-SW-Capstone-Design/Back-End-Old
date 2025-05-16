package soon.capstone.domain.meetinglog.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.meetinglog.controller.dto.MeetingLogUpdateRequest;
import soon.capstone.domain.meetinglog.service.dto.response.MeetingLogDetailResponse;
import soon.capstone.global.anootation.TestMember;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @TestMember
    @DisplayName("회의록 상세 정보를 조회한다.")
    @Test
    void getMeetingLogDetail() throws Exception {
        // given
        Long meetingLogId = 1L;
        Long teamId = 1L;
        LocalDate now = LocalDate.now();

        given(meetingLogService.getMeetingLogDetail(meetingLogId))
            .willReturn(createMeetingLogDetailResponse(meetingLogId, now));

        // expected
        mockMvc.perform(get(BASE_URL + "/{meetingLogId}", teamId, meetingLogId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(meetingLogId))
            .andExpect(jsonPath("$.content").value("content"))
            .andExpect(jsonPath("$.createdAt").value(now.toString()))
            .andExpect(jsonPath("$.title").value("title"));
    }

    @TestMember
    @DisplayName("팀 아이디로 회의록 목록을 조회한다.")
    @Test
    void getMeetingLogsByTeamId() throws Exception {
        // given
        Long teamId = 1L;
        LocalDate now = LocalDate.now();
        List<MeetingLogDetailResponse> response = List.of(
            createMeetingLogDetailResponse(1L, now),
            createMeetingLogDetailResponse(2L, now)
        );

        given(meetingLogService.getMeetingLogsByTeamId(teamId))
            .willReturn(response);

        // expected
        mockMvc.perform(get(BASE_URL, teamId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L));
    }

    private MeetingLogDetailResponse createMeetingLogDetailResponse(Long meetingLogId, LocalDate now) {
        return MeetingLogDetailResponse.builder()
            .id(meetingLogId)
            .content("content")
            .createdAt(now)
            .title("title")
            .build();
    }

}
package soon.capstone.domain.team.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.team.controller.dto.TeamCreateRequest;
import soon.capstone.global.anootation.TestMember;
import soon.capstone.global.exception.team.IsNotTeamLeaderException;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static soon.capstone.global.exception.dto.ErrorDetail.IS_NOT_TEAM_LEADER;

class TeamControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/teams";

    @TestMember
    @DisplayName("팀을 생성한다")
    @Test
    void createTeam() throws Exception {
        // given
        TeamCreateRequest request = TeamCreateRequest.builder()
            .name("testName")
            .organizationName("testOrganizationName")
            .description("testDescription")
            .build();

        given(teamService.createTeam(request.toServiceRequest(), 1L)).willReturn(1L);

        // expected
        mockMvc.perform(
                post(BASE_URL)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1));
    }

    @DisplayName("팀을 생성할 때 팀 이름은 필수값이다.")
    @Test
    void createTeamWithoutName() throws Exception {
        // given
        TeamCreateRequest request = TeamCreateRequest.builder()
            .organizationName("testOrganizationName")
            .description("testDescription")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.name").value("팀 이름을 입력해주세요"));
    }

    @DisplayName("팀을 생성할 때 팀 설명은 필수값이다.")
    @Test
    void createTeamWithoutDescription() throws Exception {
        // given
        TeamCreateRequest request = TeamCreateRequest.builder()
            .name("testName")
            .organizationName("testOrganizationName")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.description").value("팀 설명을 입력해주세요"));
    }

    @DisplayName("팀을 생성할 때 오가니제이션명은 필수값이다.")
    @Test
    void createTeamWithoutOrganizationName() throws Exception {
        // given
        TeamCreateRequest request = TeamCreateRequest.builder()
            .name("testName")
            .description("testDescription")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.organizationName").value("오가니제이션명을 입력해주세요"));
    }

    @TestMember
    @DisplayName("팀 초대 코드를 생성한다")
    @Test
    void generateInvitationCode() throws Exception {
        // given
        Long teamId = 1L;
        String mockCode = "ABCD1234";

        given(teamService.generateInvitationCode(teamId, 1L)).willReturn(mockCode);

        // expected
        mockMvc.perform(
                post(BASE_URL + "/invitation-code")
                    .content(objectMapper.writeValueAsString(teamId))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(mockCode));
    }

    @TestMember
    @DisplayName("팀 리더가 아닐 경우 초대 코드 생성 시 예외가 발생한다")
    @Test
    void generateInvitationCodeWhenNotLeader() throws Exception {
        // given
        Long teamId = 1L;

        given(teamService.generateInvitationCode(teamId, 1L))
            .willThrow(new IsNotTeamLeaderException());

        // expected
        mockMvc.perform(
                post(BASE_URL + "/invitation-code")
                    .content(objectMapper.writeValueAsString(teamId))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.message").value(IS_NOT_TEAM_LEADER.getMessage()));
    }

}
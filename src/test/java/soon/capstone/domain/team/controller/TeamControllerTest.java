package soon.capstone.domain.team.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.team.controller.dto.TeamCreateRequest;
import soon.capstone.domain.team.controller.dto.TeamInvitationRequest;
import soon.capstone.domain.team.controller.dto.TeamJoinRequest;
import soon.capstone.domain.team.service.dto.response.TeamDetailResponse;
import soon.capstone.global.anootation.TestMember;
import soon.capstone.global.exception.team.IsNotTeamLeaderException;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        String mockCode = "ABCD1234";
        Long teamId = 1L;
        Long memberId = 1L;
        given(teamService.generateInvitationCode(teamId, memberId)).willReturn(mockCode);

        // expected
        mockMvc.perform(
                post(BASE_URL + "/{teamId}/invitation-code", teamId)
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
        Long memberId = 1L;

        given(teamService.generateInvitationCode(teamId, memberId))
            .willThrow(new IsNotTeamLeaderException());

        // expected
        mockMvc.perform(
                post(BASE_URL + "/{teamId}/invitation-code", teamId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.message").value(IS_NOT_TEAM_LEADER.getMessage()));
    }

    @TestMember
    @DisplayName("팀 리더가 초대 이메일을 성공적으로 전송한다")
    @Test
    void sendInvitationEmails() throws Exception {
        // given
        Long teamId = 1L;
        TeamInvitationRequest request = TeamInvitationRequest.builder()
            .emails(List.of("test1@example.com", "test2@example.com"))
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/{teamId}/invitation-emails", teamId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @TestMember
    @DisplayName("팀 리더가 아닐 경우 이메일 전송시 예외가 발생한다")
    @Test
    void sendInvitationEmailsWhenNotLeader() throws Exception {
        // given
        Long teamId = 1L;
        TeamInvitationRequest request = TeamInvitationRequest.builder()
            .emails(List.of("test@example.com"))
            .build();

        doThrow(new IsNotTeamLeaderException())
            .when(teamService)
            .sendInvitationEmails(request.toServiceRequest(teamId), 1L);

        // expected
        mockMvc.perform(
                post(BASE_URL + "/{teamId}/invitation-emails", teamId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.message").value(IS_NOT_TEAM_LEADER.getMessage()));
    }

    @DisplayName("초대 이메일 전송 시 이메일 목록은 필수값이다")
    @Test
    void sendInvitationEmailsWithoutEmails() throws Exception {
        // given
        Long teamId = 1L;
        TeamInvitationRequest request = TeamInvitationRequest.builder()
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/{teamId}/invitation-emails", teamId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.emails").value("이메일 목록은 필수입니다."));
    }

    @DisplayName("초대 이메일 전송 시 유효하지 않은 이메일 형식이 있으면 예외가 발생한다")
    @Test
    void sendInvitationEmailsWithInvalidEmailFormat() throws Exception {
        // given
        Long teamId = 1L;
        TeamInvitationRequest request = TeamInvitationRequest.builder()
            .emails(List.of("test@example.com", "invalid-email"))
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/{teamId}/invitation-emails", teamId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."));
    }

    @TestMember
    @DisplayName("초대 코드를 사용하여 팀에 가입한다.")
    @Test
    void joinTeamWithInvitationCode() throws Exception {
        // given
        Long mockTeamId = 1L;
        var request = TeamJoinRequest.builder()
            .invitationCode("ABCD1234")
            .build();

        given(teamService.joinTeamWithInvitationCode(request.toServiceRequest(), 1L))
            .willReturn(mockTeamId);

        // expected
        mockMvc.perform(
                post(BASE_URL + "/join")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(mockTeamId));
    }

    @TestMember
    @DisplayName("팀 가입 요청시 초대코드는 필수 값이다.")
    @Test
    void joinTeamWithInvalidInvitationCode() throws Exception {
        // given
        var request = TeamJoinRequest.builder()
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/join")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.invitationCode").value("팀 초대 코드는 필수입니다."));
    }

    @TestMember
    @DisplayName("멤버가 참여한 팀 목록을 조회한다.")
    @Test
    void getTeamList() throws Exception {
        // given
        Long memberId = 1L;
        var response = List.of(
            createTeamDetailResponse(1L),
            createTeamDetailResponse(2L)
        );

        given(teamService.getTeamDetails(memberId))
            .willReturn(response);

        // expected
        mockMvc.perform(get(BASE_URL))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$").isArray());
    }

    @TestMember
    @DisplayName("참여된 팀이 없을 경우 빈 리스트를 반환한다.")
    @Test
    void getTeamListWithoutEmptyList() throws Exception {
        // given
        Long memberId = 1L;
        List<TeamDetailResponse> response = List.of();

        given(teamService.getTeamDetails(memberId))
            .willReturn(response);

        // expected
        mockMvc.perform(get(BASE_URL))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    private static TeamDetailResponse createTeamDetailResponse(long id) {
        return TeamDetailResponse.builder()
            .id(id)
            .name("testName")
            .organizationName("testOrganizationName")
            .description("testDescription")
            .build();
    }

}
package soon.capstone.domain.team.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.team.controller.dto.TeamCreateRequest;
import soon.capstone.domain.team.controller.dto.TeamGenerateInvitationCodeRequest;
import soon.capstone.domain.team.controller.dto.TeamInvitationRequest;
import soon.capstone.global.anootation.TestMember;
import soon.capstone.global.exception.team.IsNotTeamLeaderException;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
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
        TeamGenerateInvitationCodeRequest request = createGenerateInvitationCodeRequest();

        given(teamService.generateInvitationCode(request.toServiceRequest(), 1L)).willReturn(mockCode);

        // expected
        mockMvc.perform(
                post(BASE_URL + "/invitation-code")
                    .content(objectMapper.writeValueAsString(request))
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
        TeamGenerateInvitationCodeRequest request = createGenerateInvitationCodeRequest();

        given(teamService.generateInvitationCode(request.toServiceRequest(), 1L))
            .willThrow(new IsNotTeamLeaderException());

        // expected
        mockMvc.perform(
                post(BASE_URL + "/invitation-code")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.message").value(IS_NOT_TEAM_LEADER.getMessage()));
    }

    @DisplayName("초대 코드 생성에 teamId는 필수값이다")
    @Test
    void generateInvitationCodeWithoutTeamId() throws Exception {
        // given
        TeamGenerateInvitationCodeRequest request = TeamGenerateInvitationCodeRequest.builder()
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/invitation-code")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.teamId").value("팀 ID는 필수 값입니다."));
    }

    @DisplayName("초대 코드 생성에 teamId는 0보다 커야한다")
    @Test
    void generateInvitationCodeWithInvalidTeamId() throws Exception {
        // given
        TeamGenerateInvitationCodeRequest request = TeamGenerateInvitationCodeRequest.builder()
            .teamId(0L)
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/invitation-code")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.teamId").value("팀 ID는 0보다 커야 합니다."));
    }

    @TestMember
    @DisplayName("팀 리더가 초대 이메일을 성공적으로 전송한다")
    @Test
    void sendInvitationEmails() throws Exception {
        // given
        TeamInvitationRequest request = TeamInvitationRequest.builder()
            .teamId(1L)
            .emails(List.of("test1@example.com", "test2@example.com"))
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/invitation-emails")
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
        TeamInvitationRequest request = TeamInvitationRequest.builder()
            .teamId(1L)
            .emails(List.of("test@example.com"))
            .build();

        doThrow(new IsNotTeamLeaderException())
            .when(teamService)
            .sendInvitationEmails(request.toServiceRequest(), 1L);

        // expected
        mockMvc.perform(
                post(BASE_URL + "/invitation-emails")
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
        TeamInvitationRequest request = TeamInvitationRequest.builder()
            .teamId(1L)
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/invitation-emails")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.emails").value("이메일 목록은 필수입니다."));
    }

    @DisplayName("초대 이메일 전송 시 팀 ID는 필수값이다")
    @Test
    void sendInvitationEmailsWithoutTeamId() throws Exception {
        // given
        TeamInvitationRequest request = TeamInvitationRequest.builder()
            .emails(List.of("test@example.com"))
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/invitation-emails")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.teamId").value("팀 ID는 필수입니다."));
    }

    @DisplayName("초대 이메일 전송 시 유효하지 않은 이메일 형식이 있으면 예외가 발생한다")
    @Test
    void sendInvitationEmailsWithInvalidEmailFormat() throws Exception {
        // given
        TeamInvitationRequest request = TeamInvitationRequest.builder()
            .teamId(1L)
            .emails(List.of("test@example.com", "invalid-email"))
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL + "/invitation-emails")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."));
    }

    private TeamGenerateInvitationCodeRequest createGenerateInvitationCodeRequest() {
        return TeamGenerateInvitationCodeRequest.builder()
            .teamId(1L)
            .build();
    }

}
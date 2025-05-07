package soon.capstone.domain.teammember.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.teammember.controller.dto.TeamMemberUpdatePositionRequest;
import soon.capstone.domain.teammember.controller.dto.TeamMemberUpdateRoleRequest;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;
import soon.capstone.global.anootation.TestMember;
import soon.capstone.global.exception.team.TeamNotAuthorizedException;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static soon.capstone.domain.teammember.entity.common.Position.BACKEND;
import static soon.capstone.domain.teammember.entity.common.Role.ROLE_LEADER;
import static soon.capstone.domain.teammember.entity.common.Role.ROLE_MEMBER;

class TeamMemberControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/teams/{teamId}/members";

    @TestMember
    @DisplayName("팀 멤버 목록을 조회한다")
    @Test
    void getTeamMembers() throws Exception {
        // given
        Long teamId = 1L;
        List<TeamMemberDetailResponse> responses = List.of(
            createTeamMemberDetailResponse(1L, ROLE_LEADER.name()),
            createTeamMemberDetailResponse(2L, ROLE_MEMBER.name())
        );

        given(teamMemberService.getTeamMembers(teamId, 1L))
            .willReturn(responses);

        // expected
        mockMvc.perform(
                get(BASE_URL, teamId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].memberId").value(1))
            .andExpect(jsonPath("$[1].memberId").value(2));
    }

    @TestMember
    @DisplayName("권한이 없는 경우 팀 멤버 조회시 예외가 발생한다")
    @Test
    void getTeamMembersUnauthorized() throws Exception {
        // given
        Long teamId = 1L;
        given(teamMemberService.getTeamMembers(teamId, 1L))
            .willThrow(new TeamNotAuthorizedException());

        // expected
        mockMvc.perform(
                get(BASE_URL, teamId))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @TestMember
    @DisplayName("팀 멤버 권한을 업데이트한다")
    @Test
    void updateTeamMemberRole() throws Exception {
        // given
        Long teamId = 1L;
        var request = createTeamMemberUpdateRoleRequest(1L, ROLE_MEMBER.name());

        // expected
        mockMvc.perform(
                patch(BASE_URL, teamId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(teamMemberService).updateTeamMemberRole(any(), eq(1L));
    }

    @TestMember
    @DisplayName("팀 리더가 아닌 멤버가 권한을 업데이트하려고 하면 예외가 발생한다")
    @Test
    void updateTeamMemberRoleUnauthorized() throws Exception {
        // given
        Long teamId = 1L;
        var request = createTeamMemberUpdateRoleRequest(1L, ROLE_MEMBER.name());

        doThrow(new TeamNotAuthorizedException())
            .when(teamMemberService).updateTeamMemberRole(any(), eq(1L));

        // expected
        mockMvc.perform(
                patch(BASE_URL, teamId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @TestMember
    @DisplayName("팀 멤버 권한 업데이트 시 팀 멤버 ID는 필수값이다")
    @Test
    void updateTeamMemberRoleWithoutMemberId() throws Exception {
        // given
        Long teamId = 1L;
        var request = TeamMemberUpdateRoleRequest.builder()
            .role(ROLE_MEMBER.name())
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL, teamId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.teamMemberId").value("팀 멤버 ID는 필수 값입니다."));
    }

    @TestMember
    @DisplayName("팀 멤버 권한 업데이트 시 팀 멤버 ID는 양수이다.")
    @Test
    void updateTeamMemberRoleWithZeroTeamMemberId() throws Exception {
        // given
        Long teamId = 1L;
        var request = createTeamMemberUpdateRoleRequest(0L, ROLE_MEMBER.name());

        // expected
        mockMvc.perform(
                patch(BASE_URL, teamId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.teamMemberId").value("팀 멤버 ID는 0보다 커야 합니다."));
    }

    @TestMember
    @DisplayName("팀 멤버 권한 업데이트 시 권한은 필수값이다")
    @Test
    void updateTeamMemberRoleWithoutRole() throws Exception {
        // given
        Long teamId = 1L;
        TeamMemberUpdateRoleRequest request = TeamMemberUpdateRoleRequest.builder()
            .teamMemberId(1L)
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL, teamId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.role").value("권한은 필수 값입니다."));
    }

    @TestMember
    @DisplayName("팀 멤버의 역할을 변경한다.")
    @Test
    void updateTeamMemberPosition() throws Exception {
        // given
        Long teamId = 1L;
        var request = TeamMemberUpdatePositionRequest.builder()
            .teamMemberId(1L)
            .position(BACKEND.name())
            .build();

        // expected
        mockMvc.perform(
                patch(BASE_URL + "/position", teamId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isNoContent());
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

    private static TeamMemberUpdateRoleRequest createTeamMemberUpdateRoleRequest(long teamMemberId, String name) {
        return TeamMemberUpdateRoleRequest.builder()
            .teamMemberId(teamMemberId)
            .role(name)
            .build();
    }
}
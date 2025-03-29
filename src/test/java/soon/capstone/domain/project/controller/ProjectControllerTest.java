package soon.capstone.domain.project.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.project.service.dto.response.ProjectDetailResponse;
import soon.capstone.global.anootation.TestMember;
import soon.capstone.global.exception.project.ProjectNotFoundException;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerTest extends ControllerTestSupport {

    private final static String BASE_URL = "/api/v1/projects";

    @TestMember
    @DisplayName("팀의 프로젝트 목록을 조회한다.")
    @Test
    void getProjects() throws Exception {
        // given
        Long teamId = 1L;
        Long memberId = 1L;

        List<ProjectDetailResponse> projectDetailResponses = List.of(
                createProjectDetailResponse(1L, "FrontEnd"),
                createProjectDetailResponse(2L, "BackEnd")
        );

        given(projectService.getProjects(memberId, teamId))
            .willReturn(projectDetailResponses);

        // expected
        mockMvc.perform(
                        get(BASE_URL + "/{teamId}", teamId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("FrontEnd"))
                .andExpect(jsonPath("$[1].title").value("BackEnd"));
    }

    @TestMember
    @DisplayName("팀의 프로젝트 목록을 조회 시, TeamId는 실제로 존재해야 하는 값이어야 한다.")
    @Test
    void getProjectsWithRealTeamId() throws Exception {
        // given
        Long teamId = 999L;
        Long memberId = 1L;

        given(projectService.getProjects(memberId, teamId))
                .willThrow(new ProjectNotFoundException());

        // expected
        mockMvc.perform(
                        get(BASE_URL + "/{teamId}", teamId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 프로젝트를 찾을 수 없습니다."));
    }

    private ProjectDetailResponse createProjectDetailResponse(Long projectId, String title) {
        return ProjectDetailResponse.builder()
                .projectId(projectId)
                .title(title)
                .creator("creator")
                .build();
    }

}
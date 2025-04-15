package soon.capstone.domain.portfolio.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import soon.capstone.ControllerTestSupport;
import soon.capstone.domain.portfolio.controller.dto.request.PortfolioCreateRequest;
import soon.capstone.domain.portfolio.controller.dto.request.PortfolioUpdateRequest;
import soon.capstone.domain.portfolio.controller.dto.response.PortfolioDetailResponse;
import soon.capstone.domain.portfolio.controller.dto.response.PortfolioResponse;
import soon.capstone.global.anootation.TestMember;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PortfolioControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/portfolios";

    @TestMember
    @DisplayName("포트폴리오 목록을 조회한다.")
    @Test
    void getPortfolios() throws Exception {
        // given
        Long memberId = 1L;

        List<PortfolioResponse> responses = List.of(
            createPortfolioResponse("Portfolio 1", "Content 1"),
            createPortfolioResponse("Portfolio 2", "Content 2")
        );

        given(portfolioService.getPortfolios(memberId))
            .willReturn(responses);

        // expected
        mockMvc.perform(
                get(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Portfolio 1"))
            .andExpect(jsonPath("$[1].title").value("Portfolio 2"));
    }

    @TestMember
    @DisplayName("포트폴리오를 상세 조회한다.")
    @Test
    void getPortfolio() throws Exception {
        // given
        Long memberId = 1L;
        Long portfolioId = 1L;

        PortfolioDetailResponse response = PortfolioDetailResponse.builder()
            .title("Portfolio 1")
            .content("Content 1")
            .nickname("nickname")
            .profileImageURL("profileImageURL")
            .createTime(LocalDateTime.now())
            .modifyTime(LocalDateTime.now())
            .build();

        given(portfolioService.getPortfolio(memberId, portfolioId))
            .willReturn(response);

        // expected
        mockMvc.perform(
                get(BASE_URL + "/{portfolioId}", portfolioId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Portfolio 1"))
            .andExpect(jsonPath("$.content").value("Content 1"))
            .andExpect(jsonPath("$.nickname").value("nickname"));
    }

    @TestMember
    @DisplayName("포트폴리오를 생성한다.")
    @Test
    void createPortfolio() throws Exception {
        // given
        Long memberId = 1L;

        var request = PortfolioCreateRequest.builder()
            .title("New Portfolio")
            .content("New Content")
            .build();

        given(portfolioService.createPortfolio(memberId, request.toServiceRequest()))
            .willReturn(1L);

        // expected
        mockMvc.perform(
                post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(1L));
    }

    @TestMember
    @DisplayName("포트폴리오를 생성할 때, 포트폴리오 제목은 필수이다.")
    @Test
    void createPortfolioWithoutTitle() throws Exception {
        // given
        Long memberId = 1L;

        var request = PortfolioCreateRequest.builder()
            .content("New Content")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andDo(print())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.title").value("포트폴리오 제목은 필수입니다."));
    }

    @TestMember
    @DisplayName("포트폴리오를 생성할 때, 포트폴리오 내용은 필수이다.")
    @Test
    void createPortfolioWithoutContent() throws Exception {
        // given
        Long memberId = 1L;

        var request = PortfolioCreateRequest.builder()
            .title("New Portfolio")
            .build();

        // expected
        mockMvc.perform(
                post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andDo(print())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.content").value("포트폴리오 내용은 필수입니다."));
    }

    @TestMember
    @DisplayName("포트폴리오를 수정한다.")
    @Test
    void updatePortfolio() throws Exception {
        // given
        Long memberId = 1L;
        Long portfolioId = 1L;

        var request = PortfolioUpdateRequest.builder()
            .title("Updated Portfolio")
            .content("Updated Content")
            .build();

        given(portfolioService.updatePortfolio(memberId, request.toServiceRequest(portfolioId)))
            .willReturn(portfolioId);

        // expected
        mockMvc.perform(
                put(BASE_URL + "/{portfolioId}", portfolioId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(portfolioId));
    }

    @TestMember
    @DisplayName("포트폴리오를 삭제한다.")
    @Test
    void deletePortfolio() throws Exception {
        // given
        Long memberId = 1L;
        Long portfolioId = 1L;

        doNothing()
            .when(portfolioService)
            .deletePortfolio(memberId, portfolioId);

        // expected
        mockMvc.perform(
                delete(BASE_URL + "/{portfolioId}", portfolioId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    private PortfolioResponse createPortfolioResponse(String title, String content) {
        return PortfolioResponse.builder()
            .title(title)
            .content(content)
            .build();
    }

}
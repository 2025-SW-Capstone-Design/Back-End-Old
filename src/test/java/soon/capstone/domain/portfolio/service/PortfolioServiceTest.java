package soon.capstone.domain.portfolio.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.portfolio.controller.dto.request.PortfolioCreateRequest;
import soon.capstone.domain.portfolio.controller.dto.request.PortfolioUpdateRequest;
import soon.capstone.domain.portfolio.controller.dto.response.PortfolioDetailResponse;
import soon.capstone.domain.portfolio.controller.dto.response.PortfolioResponse;
import soon.capstone.domain.portfolio.entity.Portfolio;
import soon.capstone.domain.portfolio.repository.PortfolioRepository;
import soon.capstone.global.exception.portfolio.PortfolioBadRequestException;
import soon.capstone.global.exception.portfolio.PortfolioDuplicateTitleException;
import soon.capstone.global.exception.portfolio.PortfolioIsNotOwnerException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PortfolioServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioService portfolioService;

    @AfterEach
    void tearDown() {
        portfolioRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("포트폴리오를 생성한다.")
    @Test
    void createPortfolio() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Long memberId = member.getId();
        PortfolioCreateRequest request = PortfolioCreateRequest.builder()
            .title("title")
            .content("content")
            .build();

        // When
        Long portfolioId = portfolioService.createPortfolio(memberId, request.toServiceRequest());

        // Then
        Portfolio portfolio = portfolioRepository.findById(portfolioId);
        assertAll(
            () -> assertNotNull(portfolio),
            () -> assertEquals("title", portfolio.getTitle()),
            () -> assertEquals("content", portfolio.getContent()),
            () -> assertEquals(memberId, portfolio.getMember().getId())
        );

    }

    @DisplayName("포트폴리오를 생성할 때, 제목이 중복되면 예외가 발생한다.")
    @Test
    void createPortfolioWithoutDuplicateTitle() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Portfolio portfolio = createPortfolio(member);
        portfolioRepository.save(portfolio);

        Long memberId = member.getId();
        PortfolioCreateRequest request = PortfolioCreateRequest.builder()
            .title("title")
            .content("content")
            .build();

        // Expected
        assertThatThrownBy(() -> portfolioService.createPortfolio(memberId, request.toServiceRequest()))
            .isInstanceOf(PortfolioDuplicateTitleException.class)
            .hasMessage("포트폴리오 제목이 중복됩니다.");

    }

    @DisplayName("포트폴리오를 상세 조회한다.")
    @Test
    void getPortfolio() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Portfolio portfolio = createPortfolio(member);
        portfolioRepository.save(portfolio);

        Long memberId = member.getId();
        Long portfolioId = portfolio.getId();

        // When
        PortfolioDetailResponse response = portfolioService.getPortfolio(memberId, portfolioId);

        // Then
        assertThat(response)
            .extracting(PortfolioDetailResponse::title, PortfolioDetailResponse::content)
            .containsExactlyInAnyOrder(portfolio.getTitle(), portfolio.getContent());
    }

    @DisplayName("포트폴리오를 상세 조회할 때, 소유자가 아닌 사람이 조회하면 예외가 발생한다.")
    @Test
    void getPortfolioWithoutIsNotOwnerShip() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Member otherMember = createOtherMember();
        memberRepository.save(otherMember);

        Portfolio portfolio = createPortfolio(member);
        portfolioRepository.save(portfolio);

        Long memberId = otherMember.getId();
        Long portfolioId = portfolio.getId();

        // Expected
        assertThatThrownBy(() -> portfolioService.getPortfolio(memberId, portfolioId))
            .isInstanceOf(PortfolioIsNotOwnerException.class)
            .hasMessage("해당 포트폴리오의 소유자가 아닙니다.");
    }

    @DisplayName("포트폴리오 목록을 조회한다.")
    @Test
    void getPortfolios() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Portfolio portfolio = createPortfolio(member);
        portfolioRepository.save(portfolio);

        Long memberId = member.getId();

        // When
        List<PortfolioResponse> responses = portfolioService.getPortfolios(memberId);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst())
            .extracting(PortfolioResponse::title, PortfolioResponse::content)
            .containsExactlyInAnyOrder(portfolio.getTitle(), portfolio.getContent());
    }

    @DisplayName("포트폴리오를 수정한다.")
    @Test
    void updatePortfolio() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Portfolio portfolio = createPortfolio(member);
        portfolioRepository.save(portfolio);

        PortfolioUpdateRequest request = PortfolioUpdateRequest.builder()
            .title("updatedTitle")
            .content("updatedContent")
            .build();

        Long memberId = member.getId();
        Long portfolioId = portfolio.getId();

        // When
        Long updatedPortfolioId = portfolioService.updatePortfolio(memberId, request.toServiceRequest(portfolioId));

        // Then
        Portfolio updatedPortfolio = portfolioRepository.findById(updatedPortfolioId);
        assertThat(updatedPortfolio)
            .extracting("title", "content")
            .contains("updatedTitle", "updatedContent");
    }

    @DisplayName("포트폴리오를 수정할 때, 요청값이 비어있거나 null 이면 예외가 발생한다.")
    @Test
    void updatePortfolioWithoutRequestIsEmpty() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Portfolio portfolio = createPortfolio(member);
        portfolioRepository.save(portfolio);

        PortfolioUpdateRequest request = PortfolioUpdateRequest.builder()
            .title("")
            .content(null)
            .build();

        Long memberId = member.getId();
        Long portfolioId = portfolio.getId();

        // Expected
        assertThatThrownBy(() -> portfolioService.updatePortfolio(memberId, request.toServiceRequest(portfolioId)))
            .isInstanceOf(PortfolioBadRequestException.class)
            .hasMessage("제목과 내용이 비어있습니다.");
    }

    @DisplayName("포트폴리오를 수정할 때, 소유주가 아니면 예외가 발생한다.")
    @Test
    void updatePortfolioWithoutIsNotOwnership() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Member otherMember = createOtherMember();
        memberRepository.save(otherMember);

        Portfolio portfolio = createPortfolio(member);
        portfolioRepository.save(portfolio);

        PortfolioUpdateRequest request = PortfolioUpdateRequest.builder()
            .title("updatedTitle")
            .content("updatedContent")
            .build();

        Long memberId = otherMember.getId();
        Long portfolioId = portfolio.getId();

        // Expected
        assertThatThrownBy(() -> portfolioService.updatePortfolio(memberId, request.toServiceRequest(portfolioId)))
            .isInstanceOf(PortfolioIsNotOwnerException.class)
            .hasMessage("해당 포트폴리오의 소유자가 아닙니다.");
    }

    @DisplayName("포트폴리오를 수정할 때, 제목이 중복되면 예외가 발생한다.")
    @Test
    void updatePortfolioWithoutDuplicateTitle() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Portfolio portfolio = createPortfolio(member);
        portfolioRepository.save(portfolio);

        PortfolioUpdateRequest request = PortfolioUpdateRequest.builder()
            .title("title")
            .content("updatedContent")
            .build();

        Long memberId = member.getId();
        Long portfolioId = portfolio.getId();

        // Expected
        assertThatThrownBy(() -> portfolioService.updatePortfolio(memberId, request.toServiceRequest(portfolioId)))
            .isInstanceOf(PortfolioDuplicateTitleException.class)
            .hasMessage("포트폴리오 제목이 중복됩니다.");
    }

    @DisplayName("포트폴리오를 삭제한다.")
    @Test
    void deletePortfolio() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Portfolio portfolio = createPortfolio(member);
        portfolioRepository.save(portfolio);

        Long memberId = member.getId();
        Long portfolioId = portfolio.getId();

        // When
        portfolioService.deletePortfolio(memberId, portfolioId);

        // Then
        boolean isPresent = portfolioRepository.existsById(portfolioId);
        assertThat(isPresent).isFalse();
    }

    @DisplayName("포트폴리오를 삭제할 때, 소유자가 아니면 예외가 발생한다.")
    @Test
    void deletePortfolioWithoutIsNotOwnership() {
        // Given
        Member member = createMember();
        memberRepository.save(member);

        Member otherMember = createOtherMember();
        memberRepository.save(otherMember);

        Portfolio portfolio = createPortfolio(member);
        portfolioRepository.save(portfolio);

        Long memberId = otherMember.getId();
        Long portfolioId = portfolio.getId();

        // Expected
        assertThatThrownBy(() -> portfolioService.deletePortfolio(memberId, portfolioId))
            .isInstanceOf(PortfolioIsNotOwnerException.class)
            .hasMessage("해당 포트폴리오의 소유자가 아닙니다.");
    }

    private Member createMember() {
        return Member.builder()
            .email("email")
            .nickname("nickname")
            .profileImageURL("profileImageURL")
            .build();
    }

    private Member createOtherMember() {
        return Member.builder()
            .email("otherEmail")
            .nickname("otherNickname")
            .profileImageURL("otherProfileImageURL")
            .build();
    }

    private Portfolio createPortfolio(Member member) {
        return Portfolio.builder()
            .title("title")
            .content("content")
            .member(member)
            .build();
    }

}
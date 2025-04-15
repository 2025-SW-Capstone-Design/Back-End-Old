package soon.capstone.domain.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.portfolio.controller.dto.response.PortfolioDetailResponse;
import soon.capstone.domain.portfolio.controller.dto.response.PortfolioResponse;
import soon.capstone.domain.portfolio.entity.Portfolio;
import soon.capstone.domain.portfolio.repository.PortfolioRepository;
import soon.capstone.domain.portfolio.service.dto.PortfolioCreateServiceRequest;
import soon.capstone.domain.portfolio.service.dto.PortfolioUpdateServiceRequest;
import soon.capstone.global.exception.portfolio.PortfolioBadRequestException;
import soon.capstone.global.exception.portfolio.PortfolioDuplicateTitleException;
import soon.capstone.global.exception.portfolio.PortfolioIsNotOwnerException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PortfolioService {

    private final MemberRepository memberRepository;
    private final PortfolioRepository portfolioRepository;

    public Long createPortfolio(Long memberId, PortfolioCreateServiceRequest serviceRequest) {
        Member member = memberRepository.findById(memberId);

        validateDuplicatePortfolio(serviceRequest.title(), memberId);

        Portfolio portfolio = Portfolio.builder()
            .title(serviceRequest.title())
            .content(serviceRequest.content())
            .member(member)
            .build();

        portfolioRepository.save(portfolio);
        return portfolio.getId();
    }

    @Transactional(readOnly = true)
    public PortfolioDetailResponse getPortfolio(Long memberId, Long portfolioId) {
        Member member = memberRepository.findById(memberId);
        Portfolio portfolio = portfolioRepository.findById(portfolioId);

        validatePortfolioOwnership(portfolio, member);

        return PortfolioDetailResponse.builder()
            .title(portfolio.getTitle())
            .content(portfolio.getContent())
            .nickname(member.getNickname())
            .profileImageURL(member.getProfileImageURL())
            .createTime(member.getCreateTime())
            .modifyTime(member.getModifyTime())
            .build();
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponse> getPortfolios(Long memberId) {
        List<Portfolio> portfolios = portfolioRepository.findAllByMemberId(memberId);

        return portfolios.stream()
            .map(this::mapToPortfolioResponse)
            .toList();
    }

    @Transactional
    public Long updatePortfolio(Long memberId, PortfolioUpdateServiceRequest portfolioUpdateServiceRequest) {
        validateRequestNotEmpty(portfolioUpdateServiceRequest.title(), portfolioUpdateServiceRequest.content());

        Member member = memberRepository.findById(memberId);
        Portfolio portfolio = portfolioRepository.findById(portfolioUpdateServiceRequest.portfolioId());

        validatePortfolioOwnership(portfolio, member);
        validateDuplicatePortfolio(portfolioUpdateServiceRequest.title(), memberId);

        portfolio.update(portfolioUpdateServiceRequest.title(), portfolioUpdateServiceRequest.content());

        return portfolio.getId();
    }

    @Transactional
    public void deletePortfolio(Long memberId, Long portfolioId) {
        Member member = memberRepository.findById(memberId);
        Portfolio portfolio = portfolioRepository.findById(portfolioId);

        validatePortfolioOwnership(portfolio, member);

        portfolioRepository.delete(portfolio);
    }

    private void validateDuplicatePortfolio(String title, Long memberId) {
        if (portfolioRepository.existsByTitleAndMemberId(title, memberId)) {
            throw new PortfolioDuplicateTitleException();
        }
    }

    private void validateRequestNotEmpty(String title, String content) {
        if ((title == null || title.isEmpty()) && (content == null || content.isEmpty())) {
            throw new PortfolioBadRequestException();
        }
    }

    private void validatePortfolioOwnership(Portfolio portfolio, Member member) {
        if (!portfolio.getMember().getId().equals(member.getId())) {
            throw new PortfolioIsNotOwnerException();
        }
    }

    private PortfolioResponse mapToPortfolioResponse(Portfolio portfolio) {
        return PortfolioResponse.builder()
            .title(portfolio.getTitle())
            .content(portfolio.getContent())
            .createTime(portfolio.getCreateTime())
            .modifyTime(portfolio.getModifyTime())
            .build();
    }

}
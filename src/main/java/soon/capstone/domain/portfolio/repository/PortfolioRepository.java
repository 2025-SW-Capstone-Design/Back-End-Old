package soon.capstone.domain.portfolio.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.portfolio.entity.Portfolio;
import soon.capstone.global.exception.portfolio.PortfolioNotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PortfolioRepository {

    private final PortfolioJpaRepository portfolioJpaRepository;

    public void save(Portfolio portfolio) {
        portfolioJpaRepository.save(portfolio);
    }

    public boolean existsByTitleAndMemberId(String title, Long memberId) {
        return portfolioJpaRepository.existsByTitleAndMemberId(title, memberId);
    }

    public Portfolio findById(Long portfolioId) {
        return portfolioJpaRepository.findById(portfolioId)
            .orElseThrow(PortfolioNotFoundException::new);
    }

    public void delete(Portfolio portfolio) {
        portfolioJpaRepository.delete(portfolio);
    }

    public List<Portfolio> findAllByMemberId(Long memberId) {
        return portfolioJpaRepository.findAllByMemberId(memberId);
    }

    public void deleteAllInBatch() {
        portfolioJpaRepository.deleteAllInBatch();
    }

    public boolean existsById(Long portfolioId) {
        return portfolioJpaRepository.existsById(portfolioId);
    }
}
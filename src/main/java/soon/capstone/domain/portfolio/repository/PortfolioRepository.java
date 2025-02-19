package soon.capstone.domain.portfolio.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PortfolioRepository {

    private final PortfolioJpaRepository portfolioJpaRepository;

}
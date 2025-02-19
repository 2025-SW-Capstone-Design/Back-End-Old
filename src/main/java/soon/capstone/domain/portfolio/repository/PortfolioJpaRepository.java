package soon.capstone.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.portfolio.entity.Portfolio;

public interface PortfolioJpaRepository extends JpaRepository<Portfolio, Long> {

}
package soon.capstone.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soon.capstone.domain.portfolio.entity.Portfolio;

import java.util.List;

public interface PortfolioJpaRepository extends JpaRepository<Portfolio, Long> {

    boolean existsByTitleAndMemberId(String title, Long memberId);

    List<Portfolio> findAllByMemberId(Long memberId);

}
package soon.capstone.domain.issue.repository.issuelabel;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class IssueLabelListRepositoryImpl implements IssueLabelListRepository{

    private final JPAQueryFactory queryFactory;
}

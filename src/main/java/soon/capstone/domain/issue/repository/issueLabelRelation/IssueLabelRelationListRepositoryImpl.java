package soon.capstone.domain.issue.repository.issueLabelRelation;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class IssueLabelRelationListRepositoryImpl implements IssueLabelRelationListRepository {

    private final JPAQueryFactory queryFactory;

}

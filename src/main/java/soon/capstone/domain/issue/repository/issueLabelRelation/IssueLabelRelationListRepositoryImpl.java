package soon.capstone.domain.issue.repository.issueLabelRelation;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;

import java.util.List;

import static com.querydsl.core.types.Projections.fields;
import static soon.capstone.domain.issue.entity.QIssueLabel.issueLabel;
import static soon.capstone.domain.issue.entity.QIssueLabelRelation.issueLabelRelation;

@RequiredArgsConstructor
@Repository
public class IssueLabelRelationListRepositoryImpl implements IssueLabelRelationListRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<IssueLabelDetailResponse> getIssueLabelsByIssueId(Long issueId) {
        return queryFactory.select(fields(IssueLabelDetailResponse.class,
                        issueLabel.id,
                        issueLabel.title.as("name"),
                        issueLabel.color,
                        issueLabel.description
                ))
                .from(issueLabelRelation)
                .join(issueLabelRelation.issueLabel, issueLabel)
                .where(issueLabelRelation.issue.id.eq(issueId))
                .fetch();
    }

}

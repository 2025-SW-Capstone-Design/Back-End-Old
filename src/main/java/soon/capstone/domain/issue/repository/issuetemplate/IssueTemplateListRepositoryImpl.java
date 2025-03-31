package soon.capstone.domain.issue.repository.issuetemplate;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.issue.entity.IssueType;
import soon.capstone.domain.issue.service.dto.response.IssueTemplateDetailResponse;
import soon.capstone.domain.project.entity.Project;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static soon.capstone.domain.issue.entity.QIssueTemplate.issueTemplate;

@RequiredArgsConstructor
@Repository
public class IssueTemplateListRepositoryImpl implements IssueTemplateListRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<IssueTemplateDetailResponse> getIssueTemplates(String type, Project project) {
        return queryFactory.select(
                constructor(IssueTemplateDetailResponse.class,
                    issueTemplate.id,
                    issueTemplate.title,
                    issueTemplate.description,
                    issueTemplate.content,
                    enumToString(issueTemplate.type)
                ))
            .from(issueTemplate)
            .where(
                getProjectCondition(project),
                getTypeCondition(type)
            )
            .fetch();
    }

    private BooleanExpression getProjectCondition(Project project) {
        if (project == null) {
            return null;
        }
        return issueTemplate.project.eq(project);
    }

    private BooleanExpression getTypeCondition(String type) {
        if (type == null) {
            return null;
        }
        IssueType issueType = IssueType.contains(type);
        return issueTemplate.type.eq(issueType);
    }

    private StringExpression enumToString(Object enumValue) {
        return Expressions.stringTemplate("function('str', {0})", enumValue);
    }

}
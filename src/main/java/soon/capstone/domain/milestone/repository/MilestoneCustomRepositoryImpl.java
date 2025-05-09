package soon.capstone.domain.milestone.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;

import java.time.LocalDateTime;
import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static soon.capstone.domain.milestone.entity.QMilestone.milestone;

@RequiredArgsConstructor
@Repository
public class MilestoneCustomRepositoryImpl implements MilestoneCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MilestoneResponse> getMilestoneWithIssuesDueTomorrow(Long teamId) {
        LocalDateTime startOfTomorrow = LocalDateTime.now()
            .toLocalDate()
            .atStartOfDay()
            .plusDays(1);
        LocalDateTime endOfTomorrow = startOfTomorrow.plusDays(1);

        return getMilestonesDueTomorrow(teamId, startOfTomorrow, endOfTomorrow);
    }

    private List<MilestoneResponse> getMilestonesDueTomorrow(Long teamId, LocalDateTime startOfTomorrow, LocalDateTime endOfTomorrow) {
        return queryFactory
            .select(constructor(MilestoneResponse.class,
                milestone.id.as("milestoneId"),
                milestone.title,
                milestone.description,
                milestone.creator,
                milestone.dueDate,
                milestone.startDate,
                Expressions.stringTemplate("function('str', {0})", milestone.status)
                    .as("status")
            ))
            .from(milestone)
            .where(
                milestone.dueDate.between(startOfTomorrow, endOfTomorrow)
                    .and(milestone.project.team.id.eq(teamId))
            )
            .fetch();
    }

}
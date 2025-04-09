package soon.capstone.domain.milestone.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.team.entity.Team;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static soon.capstone.domain.milestone.entity.QMilestone.milestone;
import static soon.capstone.domain.project.entity.QProject.project;

@RequiredArgsConstructor
@Repository
public class MilestoneListRepositoryImpl implements MilestoneListRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MilestoneResponse> getMilestonesByProject(Project project) {
        return queryFactory.select(
                constructor(MilestoneResponse.class,
                            milestone.id.as("milestoneId"),
                            milestone.title,
                            milestone.description,
                            milestone.creator,
                            milestone.dueDate,
                            milestone.startDate,
                            milestone.isCompleted
                        ))
                .from(milestone)
                .where(milestone.project.eq(project))
                .fetch();
    }

    @Override
    public List<MilestoneResponse> getMilestonesByTeam(Team team) {
        return queryFactory.select(
                        constructor(MilestoneResponse.class,
                                milestone.id.as("milestoneId"),
                                milestone.title,
                                milestone.description,
                                milestone.creator,
                                milestone.dueDate,
                                milestone.startDate,
                                milestone.isCompleted
                        ))
                .from(milestone)
                .join(milestone.project, project)
                .where(project.team.eq(team))
                .fetch();
    }

}

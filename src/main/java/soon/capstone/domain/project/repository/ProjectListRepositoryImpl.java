package soon.capstone.domain.project.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.project.service.dto.response.ProjectDetailResponse;
import soon.capstone.domain.team.entity.Team;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static soon.capstone.domain.project.entity.QProject.project;

@RequiredArgsConstructor
@Repository
public class ProjectListRepositoryImpl implements ProjectListRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProjectDetailResponse> getProjects(Team team) {
        return queryFactory.select(
                constructor(ProjectDetailResponse.class,
                        project.id.as("projectId"),
                        project.title,
                        project.creator
                        ))
                .from(project)
                .where(project.team.eq(team))
                .fetch();
    }
}

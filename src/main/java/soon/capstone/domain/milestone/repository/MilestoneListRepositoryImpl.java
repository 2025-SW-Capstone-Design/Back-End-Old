package soon.capstone.domain.milestone.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.milestone.service.dto.MilestoneMailDto;
import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.team.entity.Team;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.constructor;
import static com.querydsl.core.types.Projections.list;
import static soon.capstone.domain.member.entity.QMember.member;
import static soon.capstone.domain.milestone.entity.QMilestone.milestone;
import static soon.capstone.domain.project.entity.QProject.project;
import static soon.capstone.domain.team.entity.QTeam.team;
import static soon.capstone.domain.teammember.entity.QTeamMember.teamMember;

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

    @Override
    public List<MilestoneMailDto> getEmailsByMilestones() {
        LocalDateTime startTime = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime dueTime = startTime.plusDays(1);

        List<Tuple> baseResults = queryFactory
            .select(milestone.title, team.name, team.id)
            .from(milestone)
            .where(milestone.dueDate.between(startTime, dueTime))
            .join(milestone.project, project)
            .join(project.team, team)
            .fetch();

        List<Tuple> emailTuples = queryFactory
            .select(team.id, member.email)
            .from(teamMember)
            .join(teamMember.member, member)
            .join(teamMember.team, team)
            .fetch();

        Map<Long, List<String>> emailMap = emailTuples.stream()
            .collect(Collectors.groupingBy(
                tuple -> tuple.get(team.id),
                Collectors.mapping(tuple -> tuple.get(member.email), Collectors.toList())
            ));

        return baseResults.stream()
            .map(tuple -> {
                String title = tuple.get(milestone.title);
                String teamName = tuple.get(team.name);
                Long teamId = tuple.get(team.id);
                List<String> emails = emailMap.getOrDefault(teamId, List.of());

                return MilestoneMailDto.builder()
                    .milestoneTitle(title)
                    .teamName(teamName)
                    .emails(emails)
                    .build();
            })
            .toList();
    }


}

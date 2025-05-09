package soon.capstone.domain.milestone.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
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
        return fetchMilestoneResponses(milestone.project.eq(project));
    }

    @Override
    public List<MilestoneResponse> getMilestonesByTeam(Team team) {
        return fetchMilestoneResponses(milestone.project.team.eq(team));
    }

    @Override
    public List<MilestoneMailDto> getEmailsByMilestones() {
        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1);

        Map<Long, List<String>> teamEmails = fetchTeamEmailMap();

        return queryFactory
            .select(milestone.title, team.name, team.id)
            .from(milestone)
            .join(milestone.project, project)
            .join(project.team, team)
            .where(milestone.dueDate.between(startOfToday, endOfToday))
            .fetch()
            .stream()
            .map(tuple -> MilestoneMailDto.builder()
                .milestoneTitle(tuple.get(milestone.title))
                .teamName(tuple.get(team.name))
                .emails(teamEmails.getOrDefault(tuple.get(team.id), List.of()))
                .build())
            .toList();
    }

    private List<MilestoneResponse> fetchMilestoneResponses(BooleanExpression condition) {
        return queryFactory.select(constructor(MilestoneResponse.class,
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
            .join(milestone.project, project)
            .where(condition)
            .fetch();
    }

    private Map<Long, List<String>> fetchTeamEmailMap() {
        return queryFactory
            .select(team.id, member.email)
            .from(teamMember)
            .join(teamMember.member, member)
            .join(teamMember.team, team)
            .fetch()
            .stream()
            .collect(Collectors.groupingBy(
                tuple -> tuple.get(team.id),
                Collectors.mapping(tuple -> tuple.get(member.email), Collectors.toList())
            ));
    }

}
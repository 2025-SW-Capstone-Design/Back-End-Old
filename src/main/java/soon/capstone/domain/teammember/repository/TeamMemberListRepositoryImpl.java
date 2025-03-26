package soon.capstone.domain.teammember.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static soon.capstone.domain.teammember.entity.QTeamMember.teamMember;

@RequiredArgsConstructor
@Repository
public class TeamMemberListRepositoryImpl implements TeamMemberListRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TeamMemberDetailResponse> getTeamMembers(Team team) {
        return queryFactory.select(
                constructor(TeamMemberDetailResponse.class,
                    teamMember.member.id.as("memberId"),
                    enumToString(teamMember.position),
                    enumToString(teamMember.role),
                    teamMember.member.nickname,
                    teamMember.member.profileImageURL
                ))
            .from(teamMember)
            .where(teamMember.team.eq(team))
            .fetch();
    }

    private StringExpression enumToString(Object enumValue) {
        return Expressions.stringTemplate("function('str', {0})", enumValue);
    }

}
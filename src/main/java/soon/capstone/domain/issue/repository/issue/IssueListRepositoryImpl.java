package soon.capstone.domain.issue.repository.issue;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.issue.entity.Issue;
import soon.capstone.domain.issue.entity.IssueLabel;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;
import soon.capstone.domain.issue.service.dto.response.IssueLabelDetailResponse;

import java.util.*;
import java.util.stream.Collectors;

import static soon.capstone.domain.issue.entity.QIssue.issue;
import static soon.capstone.domain.issue.entity.QIssueLabel.issueLabel;
import static soon.capstone.domain.issue.entity.QIssueLabelRelation.issueLabelRelation;
import static soon.capstone.domain.member.entity.QMember.member;
import static soon.capstone.domain.teammember.entity.QTeamMember.teamMember;

@RequiredArgsConstructor
@Repository
public class IssueListRepositoryImpl implements IssueListRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<IssueDetailResponse> findIssuesWithLabelsByMilestoneId(Long milestoneId) {
        List<Tuple> results = queryFactory
                .select(issue, member.nickname, issueLabel)
                .from(issue)
                .leftJoin(issue.teamMember, teamMember)
                .leftJoin(teamMember.member, member)
                .join(issueLabelRelation).on(issueLabelRelation.issue.eq(issue))
                .join(issueLabelRelation.issueLabel, issueLabel)
                .where(issue.milestone.id.eq(milestoneId))
                .fetch();

        return processQueryResults(results);
    }

    private List<IssueDetailResponse> processQueryResults(List<Tuple> results) {
        return new ArrayList<>(results.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(issue).getId(),
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                this::mapToIssueDetailResponse
                        )
                ))
                .values());
    }

    private IssueDetailResponse mapToIssueDetailResponse(List<Tuple> tuples) {
        Tuple firstTuple = tuples.getFirst();
        Issue currentIssue = firstTuple.get(issue);
        String nickname = firstTuple.get(member.nickname);

        List<IssueLabelDetailResponse> labels = tuples.stream()
                .map(this::mapToLabelResponse)
                .collect(Collectors.toList());

        return IssueDetailResponse.builder()
                .issueId(currentIssue.getId())
                .title(currentIssue.getTitle())
                .content(currentIssue.getContent())
                .creator(nickname)
                .labels(labels)
                .build();
    }

    private IssueLabelDetailResponse mapToLabelResponse(Tuple tuple) {
        IssueLabel label = tuple.get(issueLabel);
        return IssueLabelDetailResponse.builder()
                .labelId(label.getId())
                .name(label.getTitle())
                .color(label.getColor())
                .description(label.getDescription())
                .build();
    }
}
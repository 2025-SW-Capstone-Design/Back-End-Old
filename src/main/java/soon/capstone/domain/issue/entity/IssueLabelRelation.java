package soon.capstone.domain.issue.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import soon.capstone.global.common.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "issue_label_relations")
@Entity
public class IssueLabelRelation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_label_relation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_label_id")
    private IssueLabel issueLabel;

    @Builder
    private IssueLabelRelation(Issue issue, IssueLabel issueLabel) {
        this.issue = issue;
        this.issueLabel = issueLabel;
    }

    public void updateIssueLabel(IssueLabel issueLabel) {
        this.issueLabel = issueLabel;
    }

    public static IssueLabelRelation createMapping(Issue issue, IssueLabel issueLabel) {
        return IssueLabelRelation.builder()
            .issue(issue)
            .issueLabel(issueLabel)
            .build();
    }

}
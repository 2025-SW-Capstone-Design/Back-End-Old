package soon.capstone.domain.issue.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.global.common.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "issues")
@Entity
public class Issue extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_label_id")
    private IssueLabel issueLabel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_member_id")
    private TeamMember teamMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id")
    private Milestone milestone;

    @Builder
    private Issue(String title, String content, IssueLabel issueLabel, TeamMember teamMember, Milestone milestone) {
        this.title = title;
        this.content = content;
        this.issueLabel = issueLabel;
        this.teamMember = teamMember;
        this.milestone = milestone;
    }

}
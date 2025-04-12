package soon.capstone.domain.issue.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import soon.capstone.domain.milestone.entity.Milestone;
import soon.capstone.domain.project.entity.Project;
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

    @Column(nullable = false)
    private Long githubIssueNumber;

    @Column(nullable = false)
    private IssueStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_member_id")
    private TeamMember teamMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id")
    private Milestone milestone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    private Issue(String title, String content, Long githubIssueNumber, IssueStatus status, TeamMember teamMember, Milestone milestone, Project project) {
        this.title = title;
        this.content = content;
        this.githubIssueNumber = githubIssueNumber;
        this.status = status;
        this.teamMember = teamMember;
        this.milestone = milestone;
        this.project = project;
    }

    public static Issue createNewIssue(
        String title,
        String content,
        Long githubIssueNumber,
        TeamMember teamMember,
        Milestone milestone,
        Project project
    ) {
        return Issue.builder()
            .title(title)
            .content(content)
            .githubIssueNumber(githubIssueNumber)
            .status(IssueStatus.OPEN)
            .teamMember(teamMember)
            .milestone(milestone)
            .project(project)
            .build();
    }

}
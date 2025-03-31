package soon.capstone.domain.issue.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.global.common.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "issue_labels")
@Entity
public class IssueLabel extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_label_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    private IssueLabel(
        String color,
        String title,
        String description,
        Team team,
        Project project
    ) {
        this.color = color;
        this.title = title;
        this.description = description;
        this.team = team;
        this.project = project;
    }

    public static IssueLabel createIssueLabel(
        String color,
        String title,
        String description,
        Team team,
        Project project
    ) {
        return IssueLabel.builder()
            .title(title)
            .color(color)
            .description(description)
            .team(team)
            .project(project)
            .build();
    }

    public void update(String title, String description, String color) {
        this.title = title;
        this.description = description;
        this.color = color;
    }

}
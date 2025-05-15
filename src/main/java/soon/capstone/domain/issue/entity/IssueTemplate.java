package soon.capstone.domain.issue.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.global.common.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "issue_templates")
@Entity
public class IssueTemplate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_template_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IssueType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    private IssueTemplate(String title, String description, String content, IssueType type, Project project) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.type = type;
        this.project = project;
    }

    public void update(String title, String description, String content, IssueType type) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.type = type;
    }

}
package soon.capstone.domain.milestone.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.global.common.BaseTimeEntity;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "milestones")
@Entity
public class Milestone extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "milestone_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String creator;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MilestoneStatus status;

    @Column(nullable = false)
    private int githubMilestoneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    private Milestone(String title, String description, String creator, LocalDateTime dueDate, LocalDateTime startDate, MilestoneStatus status, int githubMilestoneId, Project project) {
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.dueDate = dueDate;
        this.startDate = startDate;
        this.status = status;
        this.githubMilestoneId = githubMilestoneId;
        this.project = project;
    }

    public void updateMilestone(String title, String description, LocalDateTime dueDate, LocalDateTime startDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.startDate = startDate;
    }

    public void updateStatus(MilestoneStatus status) {
        this.status = status;
    }

}
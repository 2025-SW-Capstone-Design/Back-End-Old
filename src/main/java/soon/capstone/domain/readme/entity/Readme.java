package soon.capstone.domain.readme.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.project.entity.Project;
import soon.capstone.global.common.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "readmes")
@Entity
public class Readme extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "readme_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int version;

    @Column(nullable = false)
    private boolean isLatest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    private Readme(
        String title,
        String content,
        int version,
        boolean isLatest,
        Member member,
        Project project
    ) {
        this.title = title;
        this.content = content;
        this.version = version;
        this.isLatest = isLatest;
        this.member = member;
        this.project = project;
    }

    public void markAsOld() {
        this.isLatest = false;
    }

    public static Readme createNew(
        String title,
        String content,
        int version,
        Member member,
        Project project
    ) {
        return Readme.builder()
            .title(title)
            .content(content)
            .version(version)
            .isLatest(true)
            .member(member)
            .project(project)
            .build();
    }

    public static Readme rollbackFrom(Readme source, int newVersion, Member member) {
        return Readme.builder()
            .title(source.title)
            .content(source.content)
            .version(newVersion)
            .isLatest(true)
            .member(member)
            .project(source.project)
            .build();
    }

}
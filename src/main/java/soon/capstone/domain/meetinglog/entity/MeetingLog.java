package soon.capstone.domain.meetinglog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.global.common.BaseTimeEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "meeting_logs")
@Entity
public class MeetingLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metting_log_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Builder
    private MeetingLog(String title, String content, Member member, Team team) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.team = team;
    }

    public static MeetingLog create(String content, Member member, Team team, LocalDate now) {
        return MeetingLog.builder()
            .content(content)
            .title(generateTitle(team, now))
            .member(member)
            .team(team)
            .build();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    private static String generateTitle(Team team, LocalDate now) {
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        return "회의록 - " + team.getName() + " (" + formattedDate + ")";
    }

}
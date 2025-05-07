package soon.capstone.domain.teammember.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.teammember.entity.common.Position;
import soon.capstone.domain.teammember.entity.common.Role;
import soon.capstone.global.common.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "team_members")
@Entity
public class TeamMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_member_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Position position;

    @Column(nullable = false)
    private boolean isVisible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Builder
    private TeamMember(Role role, Position position, Member member, Team team) {
        this.role = role;
        this.position = position;
        this.isVisible = true;
        this.member = member;
        this.team = team;
    }

    public static TeamMember createLeader(Member member, Team team) {
        return TeamMember.builder()
            .role(Role.ROLE_LEADER)
            .position(Position.NONE)
            .member(member)
            .team(team)
            .build();
    }

    public static TeamMember createMember(Member member, Team team) {
        return TeamMember.builder()
            .role(Role.ROLE_MEMBER)
            .position(Position.NONE)
            .member(member)
            .team(team)
            .build();
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public void updatePosition(Position position) {
        this.position = position;
    }

}
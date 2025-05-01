package soon.capstone.domain.chatroom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.global.common.BaseTimeEntity;
import soon.capstone.global.exception.common.InvalidRequest;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "chat_rooms")
@Entity
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "reserved_at", nullable = true)
    private LocalDateTime reservedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Builder
    private ChatRoom(String title, boolean active, LocalDateTime reservedAt, Team team) {
        this.title = title;
        this.active = active;
        this.reservedAt = reservedAt;
        this.team = team;
    }

    public static ChatRoom create(String title, LocalDateTime reservedAt, Team team) {
        return ChatRoom.builder()
            .title(title)
            .active(true)
            .reservedAt(reservedAt)
            .team(team)
            .build();
    }

    public void updateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidRequest("title", "제목은 비어있을 수 없습니다.");
        }

        this.title = title;
    }

    public void updateReservationTime(LocalDateTime newReservedAt) {
        if (newReservedAt == null) {
            throw new InvalidRequest();
        }

        if (newReservedAt.isBefore(LocalDateTime.now())) {
            throw new InvalidRequest("reservedAt", "예약 시간은 현재 시간보다 미래여야 합니다.");
        }

        this.reservedAt = newReservedAt;
    }

}
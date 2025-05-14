package soon.capstone.domain.chatroom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.global.common.BaseTimeEntity;
import soon.capstone.global.exception.common.InvalidRequest;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false)
    private String sid; // OpenVidu에서 생성된 방의 고유 SID

    @Builder
    private ChatRoom(String title, boolean active, Team team, String sid) {
        this.title = title;
        this.active = active;
        this.team = team;
        this.sid = sid;
    }

    public static ChatRoom create(String title, Team team, String sid) {
        return ChatRoom.builder()
            .title(title)
            .active(true)
            .team(team)
            .sid(sid)
            .build();
    }

    public void updateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidRequest("title", "제목은 비어있을 수 없습니다.");
        }

        this.title = title;
    }

    public void finish() {
        this.active = false;
    }

    public void resume() {
        this.active = true;
    }

}
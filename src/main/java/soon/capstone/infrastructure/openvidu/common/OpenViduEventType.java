package soon.capstone.infrastructure.openvidu.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OpenViduEventType {

    ROOM_STARTED("room_started"),
    TRACK_UNPUBLISHED("track_unpublished"),
    TRACK_PUBLISHED("track_published"),
    ROOM_FINISHED("room_finished"),
    PARTICIPANT_JOINED("participant_joined"),
    PARTICIPANT_LEFT("participant_left");

    private final String eventType;

    public boolean equals(String eventType) {
        return this.eventType.equals(eventType);
    }

}
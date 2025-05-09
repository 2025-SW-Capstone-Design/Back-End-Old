package soon.capstone.domain.milestone.entity;

import soon.capstone.global.exception.common.InvalidRequest;

import java.util.Arrays;

public enum MilestoneStatus {

    NOT_STARTED,
    IN_PROGRESS,
    DONE;

    public static MilestoneStatus from(String status) {
        return Arrays.stream(values())
            .filter(s -> s.name().equalsIgnoreCase(status))
            .findFirst()
            .orElseThrow(InvalidRequest::new);
    }

    public static String toState(MilestoneStatus status) {
        return (status == NOT_STARTED || status == IN_PROGRESS) ? "open" : "closed";
    }

}
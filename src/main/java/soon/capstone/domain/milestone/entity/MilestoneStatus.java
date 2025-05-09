package soon.capstone.domain.milestone.entity;

import soon.capstone.global.exception.common.InvalidRequest;

import java.util.Arrays;

public enum MilestoneStatus {

    NOT_STARTED,
    IN_PROGRESS,
    DONE;

    public static MilestoneStatus contains(String status) {
        return Arrays.stream(values())
            .filter(s -> s.name().equals(status))
            .findFirst()
            .orElseThrow(InvalidRequest::new);
    }

}
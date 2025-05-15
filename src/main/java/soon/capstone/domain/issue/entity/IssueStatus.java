package soon.capstone.domain.issue.entity;

import java.util.Arrays;

public enum IssueStatus {

    OPEN,
    CLOSED;

    public static IssueStatus from(String status) {
        return Arrays.stream(values())
            .filter(issueStatus -> issueStatus.name().equalsIgnoreCase(status))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid issue status: " + status));
    }

}
package soon.capstone.domain.issue.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import soon.capstone.global.exception.common.InvalidRequest;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum IssueType {

    Feature,
    Refactor,
    Fix,
    Custom;

    public static IssueType contains(String type) {
        return Arrays.stream(IssueType.values())
            .filter(r -> r.name().equals(type))
            .findFirst()
            .orElseThrow(InvalidRequest::new);
    }

}
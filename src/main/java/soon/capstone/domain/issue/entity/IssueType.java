package soon.capstone.domain.issue.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum IssueType {

    Feature,
    Refactor,
    Fix,
    Custom;

}
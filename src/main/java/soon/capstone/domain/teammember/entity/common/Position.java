package soon.capstone.domain.teammember.entity.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import soon.capstone.global.exception.teammember.TeamMemberPositionNotFoundException;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum Position {

    NONE, // 초기 상태
    BACKEND,
    FRONTEND,
    FULLSTACK,
    MOBILE,
    ANDROID,
    IOS,
    DEVOPS,
    DBA,
    PLANNER,
    PM,
    MARKETER,
    DESIGNER,
    QA,
    ETC;

    public static Position contains(String position) {
        return Arrays.stream(values())
            .filter(value -> value.name().equalsIgnoreCase(position))
            .findFirst()
            .orElseThrow(TeamMemberPositionNotFoundException::new);
    }

}
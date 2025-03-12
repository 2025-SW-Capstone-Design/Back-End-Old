package soon.capstone.domain.teammember.entity.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

}
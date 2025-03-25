package soon.capstone.domain.teammember.entity.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import soon.capstone.global.exception.common.InvalidRequest;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {

    ROLE_LEADER,
    ROLE_MEMBER;

    public static boolean isLeader(Role role) {
        return role == ROLE_LEADER;
    }

    public static Role contains(String role) {
        return Arrays.stream(Role.values())
            .filter(r -> r.name().equals(role))
            .findFirst()
            .orElseThrow(InvalidRequest::new);
    }

}
package soon.capstone.domain.issue.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import soon.capstone.global.exception.common.InvalidRequest;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum ScopeType {

    PROJECT("project"),
    TEAM("team");

    private final String value;

    public static ScopeType from(String value) {
        return Arrays.stream(values())
            .filter(type -> type.value.equals(value))
            .findFirst()
            .orElseThrow(() -> new InvalidRequest("scope", "유효하지 않은 ScopeType입니다."));
    }

}
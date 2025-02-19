package soon.capstone.global.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorDetail {

    // 공통
    INVALID_REQUEST(400, "잘못된 요청입니다."),

    // 회원 관련
    MEMBER_NOT_FOUND(404, "해당 회원을 찾을 수 없습니다.");

    private final int status;
    private final String message;

}
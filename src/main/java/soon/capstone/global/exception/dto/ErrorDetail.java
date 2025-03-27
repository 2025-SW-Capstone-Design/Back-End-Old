package soon.capstone.global.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorDetail {

    // 공통
    INVALID_REQUEST(400, "잘못된 요청입니다."),
    EMAIL_SEND(500, "이메일 전송 중 오류가 발생했습니다."),

    // S3
    S3_FILE_UPLOAD(500, "S3 파일 업로드 중 오류가 발생했습니다."),

    // 회원 관련
    MEMBER_NOT_FOUND(404, "해당 회원을 찾을 수 없습니다."),
    UNAUTHORIZED(401, "인증 정보가 없습니다."),

    // 토큰 관련
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(404, "해당 토큰을 찾을 수 없습니다"),

    // 깃허브 관련
    GITHUB_MEMBER_EMAIL_NOT_FOUND(404, "Github 회원의 이메일을 찾을 수 없습니다."),
    PRIMARY_VERIFIED_EMAIL_NOT_FOUND(404, "Primary Verified Email을 찾을 수 없습니다."),
    OAUTH_TOKEN_EXPIRED(401, "OAuth 토큰이 만료되었습니다."),
    GITHUB_HTTP_CLIENT_ERROR(500, "Github 통신 중 오류가 발생했습니다."),

    // 팀 관련
    IS_NOT_ADMIN_IN_ORGANIZATION(403, "해당 조직의 관리자가 아닙니다."),
    TEAM_NOT_FOUND(404, "해당 팀을 찾을 수 없습니다."),
    TEAM_ALREADY_EXISTS(409, "해당 팀이 이미 존재합니다."),
    INVITATION_CODE_NOT_FOUND(404, "해당 초대 코드를 찾을 수 없습니다."),
    TEAM_NOT_AUTHORIZED(403, "해당 팀에 접근할 권한이 없습니다."),

    // 팀 멤버 관련
    TEAM_MEMBER_NOT_FOUND(404, "해당 팀 멤버를 찾을 수 없습니다."),
    TEAM_MEMBER_ALREADY_EXISTS(409, "해당 팀 멤버가 이미 존재합니다."),
    IS_NOT_TEAM_LEADER(403, "해당 팀의 리더가 아닙니다."),

    // 프로젝트 관련
    PROJECT_NOT_FOUND(404, "해당 프로젝트를 찾을 수 없습니다."),

    ;

    private final int status;
    private final String message;

}
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
    GITHUB_ISSUE_LABEL_NOT_FOUND(404, "해당 이슈 라벨을 찾을 수 없습니다."),

    // 팀 관련
    IS_NOT_ADMIN_IN_ORGANIZATION(403, "해당 조직의 관리자가 아닙니다."),
    TEAM_NOT_FOUND(404, "해당 팀을 찾을 수 없습니다."),
    TEAM_ALREADY_EXISTS(409, "해당 팀이 이미 존재합니다."),
    INVITATION_CODE_NOT_FOUND(404, "해당 초대 코드를 찾을 수 없습니다."),
    TEAM_NOT_AUTHORIZED(403, "해당 팀에 접근할 권한이 없습니다."),

    // 팀 멤버 관련
    TEAM_MEMBER_NOT_FOUND(404, "해당 팀 멤버를 찾을 수 없습니다."),
    TEAM_MEMBER_POSITION_NOT_FOUND(404, "해당 포지션을 찾을 수 없습니다."),
    TEAM_MEMBER_ALREADY_EXISTS(409, "해당 팀 멤버가 이미 존재합니다."),
    IS_NOT_TEAM_LEADER(403, "해당 팀의 리더가 아닙니다."),

    // 프로젝트 관련
    PROJECT_NOT_FOUND(404, "해당 프로젝트를 찾을 수 없습니다."),

    // 이슈 관련
    ISSUE_NOT_FOUND(404, "해당 이슈를 찾을 수 없습니다."),
    ISSUE_LABEL_ALREADY_EXISTS(409, "해당 이슈 라벨이 이미 존재합니다."),
    ISSUE_LABEL_NOT_FOUND(404, "해당 이슈 라벨을 찾을 수 없습니다."),
    ISSUE_TEMPLATE_NOT_FOUND(404, "해당 이슈 템플릿을 찾을 수 없습니다."),
    ISSUE_TEMPLATE_ALREADY_EXISTS(409, "해당 이슈 템플릿이 이미 존재합니다."),

    // 마일스톤 관련
    MILESTONE_NOT_FOUND(404, "해당 마일스톤을 찾을 수 없습니다."),
    MILESTONE_INVALID_DATE(400, "마일스톤의 시작일과 종료일이 올바르지 않습니다."),
    MILESTONE_DUPLICATE_TITLE(409, "마일스톤 제목이 중복됩니다."),

    // README 관련
    READEME_NOT_FOUND(404, "해당 README를 찾을 수 없습니다."),

    // 포트폴리오 관련
    PORTFOLIO_BAD_REQUEST(400, "제목과 내용이 비어있습니다."),
    PORTFOLIO_IS_NOT_OWNER(403, "해당 포트폴리오의 소유자가 아닙니다."),
    PORTFOLIO_NOT_FOUND(404, "해당 포트폴리오를 찾을 수 없습니다."),
    PORTFOLIO_DUPLICATE_TITLE(409, "포트폴리오 제목이 중복됩니다."),

    // 채팅방 관련
    CHAT_ROOM_NOT_FOUND(404, "해당 채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_TEAM_MEMBER_NOT_FOUND(404, "해당 채팅방에서 팀 멤버를 찾을 수 없습니다."),
    CHAT_ROOM_ALREADY_EXISTS_FOR_TEAM(409, "해당 팀에 이미 존재하는 채팅방입니다."),

    // openvidu 관련
    TEMPORARY_ROOM_IDENTITY_NOT_FOUND(404, "임시 방 아이디를 찾을 수 없습니다."),

    // 회의록 관련
    MEETING_LOG_NOT_FOUND(404, "해당 회의록을 찾을 수 없습니다."),
    ;

    private final int status;
    private final String message;

}
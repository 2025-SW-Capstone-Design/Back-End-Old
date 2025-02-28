package soon.capstone.global.domain.token.common;

import lombok.Getter;

@Getter
public enum TokenExpiration {

    ACCESS_TOKEN(30 * 60 * 10000L), // 30분
    REFRESH_TOKEN(7 * 24 * 60 * 60 * 1000L); // 1주일

    private final long expirationTime;

    TokenExpiration(long expirationTime) {
        this.expirationTime = expirationTime;
    }

}
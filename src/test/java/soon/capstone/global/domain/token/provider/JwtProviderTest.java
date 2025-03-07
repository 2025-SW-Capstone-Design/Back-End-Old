package soon.capstone.global.domain.token.provider;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.global.domain.token.dto.response.TokenResponse;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest extends IntegrationTestSupport {

    private JwtProvider jwtProvider;

    @Value("${spring.jwt.secretKey}")
    private String secretKey;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(secretKey);
    }

    @DisplayName("액세스 토큰과 리프레시 토큰을 생성한다.")
    @Test
    void generateAllToken() {
        // given
        String nickname = "test-nickname";

        // when
        TokenResponse tokenResponse = jwtProvider.generateAllToken(nickname);

        // then
        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.accessToken()).isNotBlank();
        assertThat(tokenResponse.refreshToken()).isNotBlank();
    }

    @DisplayName("액세스 토큰의 subject를 가져온다.")
    @Test
    void getSubjectFromToken() {
        // given
        String nickname = "test-nickname";
        TokenResponse tokenResponse = jwtProvider.generateAllToken(nickname);

        // when
        String subjectFromToken = jwtProvider.getSubjectFromToken(tokenResponse.accessToken());

        // then
        assertThat(subjectFromToken).isEqualTo(nickname);
    }

    @DisplayName("유효한 토큰을 검증한다.")
    @Test
    void validateTokenValidToken() {
        // given
        String nickname = "test-nickname";
        TokenResponse tokenResponse = jwtProvider.generateAllToken(nickname);
        String validToken = tokenResponse.accessToken();

        // when
        boolean isValid = jwtProvider.validateToken(validToken);

        // then
        assertThat(isValid).isTrue();
    }

    @DisplayName("유효하지 않은 토큰을 검증한다.")
    @Test
    void validateTokenInvalidToken() {
        // given
        String invalidToken = "invalid.token.value";

        // when
        boolean isValid = jwtProvider.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("토큰이 만료되었는지 확인한다.")
    void expiredTokenTest() throws Exception {
        // given
        Key key = getDeclaredFieldKey();
        String expiredToken = Jwts.builder()
            .setSubject("test-subject")
            .setExpiration(new Date(System.currentTimeMillis() - 1000))
            .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();

        // when
        boolean result = jwtProvider.validateToken(expiredToken);

        // then
        assertThat(result).isFalse();
    }

    private Key getDeclaredFieldKey() throws Exception {
        Field keyField = jwtProvider.getClass().getDeclaredField("key");
        keyField.setAccessible(true);
        return (Key) keyField.get(jwtProvider);
    }

}
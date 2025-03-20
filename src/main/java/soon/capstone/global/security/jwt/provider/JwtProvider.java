package soon.capstone.global.security.jwt.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import soon.capstone.domain.member.entity.Role;
import soon.capstone.global.security.jwt.dto.response.TokenResponse;

import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static soon.capstone.domain.member.entity.Role.ROLE_USER;
import static soon.capstone.global.security.jwt.common.TokenExpiration.ACCESS_TOKEN;
import static soon.capstone.global.security.jwt.common.TokenExpiration.REFRESH_TOKEN;
import static soon.capstone.global.security.jwt.common.TokenType.AUTHORIZATION_HEADER;
import static soon.capstone.global.exception.dto.ErrorDetail.INVALID_TOKEN;

@Slf4j
@Component
public class JwtProvider {

    private final Key key;

    public JwtProvider(@Value("${spring.jwt.secretKey}") String secretKey) {
        byte[] decode = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(decode);
    }

    public boolean validateToken(String token) {
        try {
            Claims claimsFromToken = getClaimsFromToken(token);
            isExpiredToken(claimsFromToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다. Token: {}", token);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT, 만료된 JWT 입니다. Token: {}", token);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT, 지원되지 않는 JWT 입니다. Token: {}", token);
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims is empty, 잘못된 JWT 입니다. Token: {}", token);
        }
        return false;
    }

    public TokenResponse generateAllToken(Long memberId) {
        String accessToken = generateAccessToken(memberId);
        String refreshToken = generateRefreshToken();

        return TokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    public String getSubjectFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaimsFromToken(token);
        String memberId = claims.getSubject();

        Role role = getRoleFromToken(token);
        Collection<SimpleGrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(role.name())
        );

        return new UsernamePasswordAuthenticationToken(memberId, "", authorities);
    }

    public Role getRoleFromToken(String token) {
        Claims claimsFromToken = getClaimsFromToken(token);
        return Role.valueOf(claimsFromToken.get(AUTHORIZATION_HEADER.getValue(), String.class));
    }

    private String generateAccessToken(Long memberId) {
        Date expirationDate = createExpirationDate(ACCESS_TOKEN.getExpirationTime());
        return Jwts.builder()
            .setSubject(String.valueOf(memberId))
            .claim(AUTHORIZATION_HEADER.getValue(), ROLE_USER)
            .setExpiration(expirationDate)
            .setIssuedAt(new Date())
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }

    private String generateRefreshToken() {
        Date expirationDate = createExpirationDate(REFRESH_TOKEN.getExpirationTime());
        return Jwts.builder()
            .setExpiration(expirationDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }

    private Date createExpirationDate(long expirationTime) {
        long currentTimeMillis = System.currentTimeMillis();
        return new Date(currentTimeMillis + expirationTime);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private void isExpiredToken(Claims claims) {
        Date expiration = claims.getExpiration();
        if (expiration.before(new Date())) {
            throw new ExpiredJwtException(null, claims, INVALID_TOKEN.getMessage());
        }
    }

}
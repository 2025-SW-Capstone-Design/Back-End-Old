package soon.capstone.global.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import soon.capstone.global.security.jwt.provider.JwtProvider;
import soon.capstone.global.exception.dto.response.ErrorResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static soon.capstone.global.security.jwt.common.TokenType.AUTHORIZATION_HEADER;
import static soon.capstone.global.security.jwt.common.TokenType.BEARER_PREFIX;
import static soon.capstone.global.exception.dto.ErrorDetail.INVALID_TOKEN;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String tokenFromHeader = getTokenFromHeader(request);
        if (tokenFromHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtProvider.validateToken(tokenFromHeader)) {
                Authentication authentication = jwtProvider.getAuthentication(tokenFromHeader);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } else {
                responseJWTError(response);
            }
        } catch (Exception e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
            responseJWTError(response);
        }
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String tokenFromHeader = request.getHeader(AUTHORIZATION_HEADER.getValue());
        if (StringUtils.hasText(tokenFromHeader) && tokenFromHeader.startsWith(BEARER_PREFIX.getValue())) {
            return tokenFromHeader.substring(BEARER_PREFIX.getValue().length());
        }

        return null;
    }

    private void responseJWTError(HttpServletResponse response) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
            .status(SC_UNAUTHORIZED)
            .message(INVALID_TOKEN.getMessage())
            .build();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/api/v1/open-vidu/webhook");
    }

}
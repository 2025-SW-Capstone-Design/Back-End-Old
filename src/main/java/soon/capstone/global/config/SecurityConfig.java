package soon.capstone.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import soon.capstone.global.domain.token.filter.JwtAuthenticationFilter;
import soon.capstone.global.domain.token.handler.JwtAccessDeniedHandler;
import soon.capstone.global.domain.token.handler.JwtAuthenticationEntryPoint;
import soon.capstone.global.domain.token.provider.JwtProvider;
import soon.capstone.global.oauth2.handler.OAuth2FailureHandler;
import soon.capstone.global.oauth2.handler.OAuth2SuccessHandler;
import soon.capstone.global.oauth2.service.OAuth2GithubService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2GithubService oauth2GithubService;
    private final OAuth2SuccessHandler oauth2SuccessHandler;
    private final OAuth2FailureHandler oauth2FailureHandler;
    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .cors(cors -> cors.configurationSource(corsConfig()));

        http
            .authorizeHttpRequests(auth -> {
                auth
                    .requestMatchers(
                        "/oauth2/**", "/login/oauth2/**", "/api/v1/auth/reissue",
                        "/swagger-ui/**", "/v3/api-docs/**"
                    ).permitAll()
                    .anyRequest().authenticated();
            });

        http
            .oauth2Login(oauth2 -> {
                oauth2
                    .userInfoEndpoint(userInfoEndpointConfig ->
                        userInfoEndpointConfig.userService(oauth2GithubService))
                    .successHandler(oauth2SuccessHandler)
                    .failureHandler(oauth2FailureHandler);
            });

        http
            .exceptionHandling(exception -> {
                exception
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(jwtAccessDeniedHandler);
            });

        http
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtProvider, objectMapper),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    private CorsConfigurationSource corsConfig() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(
            List.of("*")// TODO : 배포 시 수정
        );
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
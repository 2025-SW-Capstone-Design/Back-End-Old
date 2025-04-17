package soon.capstone.global.swagger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String AUTH_NAME = "Json Web Token";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .addSecurityItem(createSecurityRequirement())
            .components(createComponents())
            .info(createInfo())
            .servers(createServers());
    }

    private Info createInfo() {
        return new Info()
            .title("Capstone Design API Document")
            .version("v0.0.1")
            .description("Capstone Design API 문서");
    }

    private SecurityRequirement createSecurityRequirement() {
        return new SecurityRequirement().addList(AUTH_NAME);
    }

    private Components createComponents() {
        return new Components()
            .addSecuritySchemes(
                AUTH_NAME,
                new SecurityScheme()
                    .name(AUTH_NAME)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("Bearer")
                    .bearerFormat("JWT")
                    .description("Access Token 토큰을 입력해주세요.(Bearer X)")
            );
    }

    private List<Server> createServers() {
        Server prodServer = new Server()
            .description("Production Server")
            .url("https://test.com");

        Server localServer = new Server()
            .description("Development Server")
            .url("http://localhost:8080");

        return Arrays.asList(prodServer, localServer);
    }
}
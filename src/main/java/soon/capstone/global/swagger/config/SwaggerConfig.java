package soon.capstone.global.swagger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import soon.capstone.global.exception.dto.ErrorDetail;
import soon.capstone.global.exception.dto.response.ErrorResponse;
import soon.capstone.global.swagger.ExampleHolder;
import soon.capstone.global.swagger.annotation.ApiExceptions;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SwaggerConfig {

    @Value("${spring.swagger.base-url}")
    private String baseUrl;
    private static final String AUTH_NAME = "Json Web Token";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .addSecurityItem(createSecurityRequirement())
            .components(createComponents())
            .info(createInfo())
            .servers(createServers());
    }

    @Bean
    public OperationCustomizer customize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            ApiExceptions apiExceptions = handlerMethod.getMethodAnnotation(
                ApiExceptions.class);

            if (apiExceptions != null) {
                generateErrorCodeResponseExample(operation, apiExceptions.value());
            }

            return operation;
        };
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
            .url(baseUrl);

        Server localServer = new Server()
            .description("Development Server")
            .url("http://localhost:8080");

        return Arrays.asList(prodServer, localServer);
    }

    private void generateErrorCodeResponseExample(Operation operation, ErrorDetail[] errorDetails) {
        ApiResponses responses = operation.getResponses();

        Map<Integer, List<ExampleHolder>> statusWithExampleHolders = Arrays.stream(errorDetails)
            .map(
                errorDetail -> ExampleHolder.builder()
                    .holder(getSwaggerExample(errorDetail))
                    .code(errorDetail.getStatus())
                    .name(errorDetail.name())
                    .build()
            )
            .collect(Collectors.groupingBy(ExampleHolder::code));

        addExamplesToResponses(responses, statusWithExampleHolders);
    }

    private Example getSwaggerExample(ErrorDetail errorDetail) {
        ErrorResponse errorResponse = ErrorResponse.builder()
            .status(errorDetail.getStatus())
            .message(errorDetail.getMessage())
            .build();
        Example example = new Example();
        example.setValue(errorResponse);

        return example;
    }

    private void addExamplesToResponses(ApiResponses responses, Map<Integer, List<ExampleHolder>> statusWithExampleHolders) {
        statusWithExampleHolders.forEach(
            (status, v) -> {
                Content content = new Content();
                MediaType mediaType = new MediaType();
                ApiResponse apiResponse = new ApiResponse();

                v.forEach(
                    exampleHolder -> mediaType.addExamples(
                        exampleHolder.name(),
                        exampleHolder.holder()
                    )
                );
                content.addMediaType("application/json", mediaType);
                apiResponse.setContent(content);
                responses.addApiResponse(String.valueOf(status), apiResponse);
            }
        );
    }

}
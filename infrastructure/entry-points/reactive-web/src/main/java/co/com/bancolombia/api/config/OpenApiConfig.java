package co.com.bancolombia.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String API_TITLE = "Loan Application API";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = "API for managing loan applications";
    private static final String PUBLIC_GROUP = "public";
    private static final String API_PATH_PATTERN = "/api/v1/**";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(API_TITLE)
                        .version(API_VERSION)
                        .description(API_DESCRIPTION));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group(PUBLIC_GROUP)
                .pathsToMatch(API_PATH_PATTERN)
                .build();
    }
}
package co.com.bancolombia.api.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    private static final String POST_METHOD = "POST";
    private static final String GET_METHOD = "GET";
    private static final String ALL_PATHS = "/**";
    private static final String ORIGINS_SEPARATOR = ",";

    @Bean
    CorsWebFilter corsWebFilter(@Value("${cors.allowed-origins}") String origins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of(origins.split(ORIGINS_SEPARATOR)));
        config.setAllowedMethods(Arrays.asList(POST_METHOD, GET_METHOD));
        config.setAllowedHeaders(List.of(CorsConfiguration.ALL));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(ALL_PATHS, config);

        return new CorsWebFilter(source);
    }
}

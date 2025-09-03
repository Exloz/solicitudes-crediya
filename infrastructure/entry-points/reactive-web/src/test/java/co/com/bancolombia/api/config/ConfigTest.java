package co.com.bancolombia.api.config;

import co.com.bancolombia.api.Handler;
import co.com.bancolombia.api.RouterRest;
import co.com.bancolombia.api.mapper.LoanApplicationMapper;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.usecase.loanapplication.LoanApplicationUseCasePort;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = ConfigTest.TestConfig.class)
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LoanApplicationUseCasePort loanApplicationUseCase;

    @MockitoBean
    private Validator validator;

    @Test
    void securityHeadersShouldBePresent() {
        // Mock the use case to avoid dependency issues
        when(validator.validate(any())).thenReturn(java.util.Collections.emptySet());
        when(loanApplicationUseCase.registerLoanApplication(any(LoanApplication.class), any(String.class)))
                .thenReturn(Mono.just(LoanApplication.builder().build()));

        // Test with POST request to check security headers
        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer valid.jwt.token")
                .bodyValue("{}")
                .exchange()
                .expectStatus().isOk() // Endpoint processes the request successfully
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

    @Configuration
    @Import({RouterRest.class, Handler.class, CorsConfig.class, SecurityHeadersConfig.class})
    static class TestConfig {

        @Bean
        public LoanApplicationMapper loanApplicationMapper() {
            return org.mapstruct.factory.Mappers.getMapper(LoanApplicationMapper.class);
        }
    }
}
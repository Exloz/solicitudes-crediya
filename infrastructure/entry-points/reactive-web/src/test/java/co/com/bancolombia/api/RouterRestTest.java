package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.LoanApplicationRequest;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = RouterRestTest.TestConfig.class)
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LoanApplicationUseCasePort loanApplicationUseCase;

    @MockitoBean
    private Validator validator;

    @Test
    void testListenPOSTUseCase() {
        // Arrange
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .build();

        LoanApplication savedApplication = LoanApplication.builder()
                .id(java.util.UUID.randomUUID())
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .statusId(1L)
                .createdAt(java.time.LocalDateTime.now())
                .build();

        when(validator.validate(any())).thenReturn(java.util.Collections.emptySet());
        when(loanApplicationUseCase.registerLoanApplication(any(LoanApplication.class), any(String.class)))
                .thenReturn(Mono.just(savedApplication));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer valid.jwt.token")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.clientId").isEqualTo("client123")
                .jsonPath("$.amount").isEqualTo(50000)
                .jsonPath("$.term").isEqualTo(12)
                .jsonPath("$.loanTypeId").isEqualTo(1);
    }

    @Configuration
    @Import({RouterRest.class, Handler.class})
    static class TestConfig {

        @Bean
        public LoanApplicationMapper loanApplicationMapper() {
            return org.mapstruct.factory.Mappers.getMapper(LoanApplicationMapper.class);
        }
    }
}

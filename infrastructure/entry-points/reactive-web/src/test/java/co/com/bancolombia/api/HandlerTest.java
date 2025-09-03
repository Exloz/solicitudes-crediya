package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.LoanApplicationRequest;
import co.com.bancolombia.api.dto.LoanApplicationResponse;
import co.com.bancolombia.api.mapper.LoanApplicationMapper;
import co.com.bancolombia.model.exception.InvalidLoanAmountException;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.usecase.loanapplication.LoanApplicationUseCasePort;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class HandlerTest {

    @Mock
    private LoanApplicationUseCasePort loanApplicationUseCase;

    @Mock
    private Validator validator;

    @Mock
    private LoanApplicationMapper mapper;

    @Mock
    private ServerRequest serverRequest;

    private Handler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new Handler(loanApplicationUseCase, validator, mapper);
    }

    @Test
    void registerLoanApplication_success() {
        // Arrange
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .build();

        LoanApplication model = LoanApplication.builder()
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .build();

        LoanApplication savedModel = LoanApplication.builder()
                .id(UUID.randomUUID())
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .status(1L)
                .createdAt(LocalDateTime.now())
                .build();

        LoanApplicationResponse response = LoanApplicationResponse.builder()
                .id(savedModel.getId())
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .status(1L)
                .createdAt(savedModel.getCreatedAt())
                .build();

        when(serverRequest.bodyToMono(LoanApplicationRequest.class)).thenReturn(Mono.just(request));
        when(validator.validate(request)).thenReturn(new HashSet<>());
        when(mapper.toModel(request)).thenReturn(model);
        when(loanApplicationUseCase.registerLoanApplication(model)).thenReturn(Mono.just(savedModel));
        when(mapper.toResponse(savedModel)).thenReturn(response);

        // Act
        Mono<ServerResponse> result = handler.registerLoanApplication(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void registerLoanApplication_validationError() {
        // Arrange
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .clientId("") // Invalid: blank
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .build();

        Set<ConstraintViolation<LoanApplicationRequest>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));

        when(serverRequest.bodyToMono(LoanApplicationRequest.class)).thenReturn(Mono.just(request));
        when(validator.validate(request)).thenReturn(violations);

        // Act
        Mono<ServerResponse> result = handler.registerLoanApplication(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void registerLoanApplication_businessLogicError() {
        // Arrange
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .build();

        LoanApplication model = LoanApplication.builder()
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .build();

        when(serverRequest.bodyToMono(LoanApplicationRequest.class)).thenReturn(Mono.just(request));
        when(validator.validate(request)).thenReturn(new HashSet<>());
        when(mapper.toModel(request)).thenReturn(model);
        when(loanApplicationUseCase.registerLoanApplication(model))
                .thenReturn(Mono.error(new InvalidLoanAmountException("Amount is invalid")));

        // Act
        Mono<ServerResponse> result = handler.registerLoanApplication(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectError(InvalidLoanAmountException.class)
                .verify();
    }
}
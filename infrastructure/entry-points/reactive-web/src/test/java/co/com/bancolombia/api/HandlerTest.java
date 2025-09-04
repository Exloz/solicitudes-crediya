package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.LoanApplicationRequest;
import co.com.bancolombia.api.dto.LoanApplicationResponse;
import co.com.bancolombia.api.dto.LoanApplicationReviewResponse;
import co.com.bancolombia.api.mapper.LoanApplicationMapper;
import co.com.bancolombia.consumer.service.AuthorizationService;
import co.com.bancolombia.model.exception.business.InvalidLoanAmountException;
import co.com.bancolombia.model.exception.security.ExpiredJwtTokenException;
import co.com.bancolombia.model.exception.security.InsufficientPrivilegesException;
import co.com.bancolombia.model.exception.security.InvalidJwtTokenException;
import co.com.bancolombia.model.exception.security.MissingAuthorizationHeaderException;
import co.com.bancolombia.model.exception.security.UserIdMismatchException;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.LoanApplicationReview;
import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.state.State;
import co.com.bancolombia.model.user.UserInfo;
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
    private AuthorizationService authorizationService;

    @Mock
    private ServerRequest serverRequest;

    @Mock
    private ServerRequest.Headers headers;

    private Handler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new Handler(loanApplicationUseCase, validator, mapper, authorizationService);
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

        String jwtToken = "Bearer valid.jwt.token";

        when(serverRequest.bodyToMono(LoanApplicationRequest.class)).thenReturn(Mono.just(request));
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn(jwtToken);
        when(validator.validate(request)).thenReturn(new HashSet<>());
        when(mapper.toModel(request)).thenReturn(model);
        when(loanApplicationUseCase.registerLoanApplication(model, "valid.jwt.token")).thenReturn(Mono.just(savedModel));
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
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(validator.validate(request)).thenReturn(violations);

        // Act
        Mono<ServerResponse> result = handler.registerLoanApplication(serverRequest);

        // Assert - GlobalExceptionHandler converts IllegalArgumentException to BAD_REQUEST
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();
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
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(validator.validate(request)).thenReturn(new HashSet<>());
        when(mapper.toModel(request)).thenReturn(model);
        // Mock the UseCase to return business logic error after JWT validation
        when(loanApplicationUseCase.registerLoanApplication(model, "valid.jwt.token"))
                .thenReturn(Mono.error(new InvalidLoanAmountException("Amount is invalid")));

        // Act
        Mono<ServerResponse> result = handler.registerLoanApplication(serverRequest);

        // Assert - GlobalExceptionHandler converts InvalidLoanAmountException to BAD_REQUEST
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    void registerLoanApplication_missingAuthorizationHeader() {
        // Arrange
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .build();

        when(serverRequest.bodyToMono(LoanApplicationRequest.class)).thenReturn(Mono.just(request));
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn(null); // Missing header

        // Act
        Mono<ServerResponse> result = handler.registerLoanApplication(serverRequest);

        // Assert - MissingAuthorizationHeaderException should return 401 Unauthorized
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.UNAUTHORIZED))
                .verifyComplete();
    }

    @Test
    void registerLoanApplication_invalidAuthorizationHeaderFormat() {
        // Arrange
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .clientId("client123")
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .build();

        when(serverRequest.bodyToMono(LoanApplicationRequest.class)).thenReturn(Mono.just(request));
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn("InvalidFormat"); // Invalid format

        // Act
        Mono<ServerResponse> result = handler.registerLoanApplication(serverRequest);

        // Assert - MissingAuthorizationHeaderException should return 401 Unauthorized
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.UNAUTHORIZED))
                .verifyComplete();
    }

    @Test
    void registerLoanApplication_invalidJwtToken() {
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
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn("Bearer invalid.jwt.token");
        when(validator.validate(request)).thenReturn(new HashSet<>());
        when(mapper.toModel(request)).thenReturn(model);
        when(loanApplicationUseCase.registerLoanApplication(model, "invalid.jwt.token"))
                .thenReturn(Mono.error(new InvalidJwtTokenException("Invalid JWT signature")));

        // Act
        Mono<ServerResponse> result = handler.registerLoanApplication(serverRequest);

        // Assert - InvalidJwtTokenException should return 401 Unauthorized
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.UNAUTHORIZED))
                .verifyComplete();
    }

    @Test
    void registerLoanApplication_expiredJwtToken() {
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
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn("Bearer expired.jwt.token");
        when(validator.validate(request)).thenReturn(new HashSet<>());
        when(mapper.toModel(request)).thenReturn(model);
        when(loanApplicationUseCase.registerLoanApplication(model, "expired.jwt.token"))
                .thenReturn(Mono.error(new ExpiredJwtTokenException("JWT token has expired")));

        // Act
        Mono<ServerResponse> result = handler.registerLoanApplication(serverRequest);

        // Assert - ExpiredJwtTokenException should return 401 Unauthorized
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.UNAUTHORIZED))
                .verifyComplete();
    }

    @Test
    void registerLoanApplication_insufficientPrivileges() {
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
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(validator.validate(request)).thenReturn(new HashSet<>());
        when(mapper.toModel(request)).thenReturn(model);
        when(loanApplicationUseCase.registerLoanApplication(model, "valid.jwt.token"))
                .thenReturn(Mono.error(new InsufficientPrivilegesException("User does not have required USER role")));

        // Act
        Mono<ServerResponse> result = handler.registerLoanApplication(serverRequest);

        // Assert - InsufficientPrivilegesException should return 403 Forbidden
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.FORBIDDEN))
                .verifyComplete();
    }

    @Test
    void registerLoanApplication_userIdMismatch() {
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
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(validator.validate(request)).thenReturn(new HashSet<>());
        when(mapper.toModel(request)).thenReturn(model);
        when(loanApplicationUseCase.registerLoanApplication(model, "valid.jwt.token"))
                .thenReturn(Mono.error(new UserIdMismatchException("User ID in token does not match requested user ID")));

        // Act
        Mono<ServerResponse> result = handler.registerLoanApplication(serverRequest);

        // Assert - UserIdMismatchException should return 403 Forbidden
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.FORBIDDEN))
                .verifyComplete();
    }

    @Test
    void getLoanApplicationsForReview_success() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        int page = 0;
        int size = 10;

        LoanApplicationReview review = LoanApplicationReview.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanType(LoanType.builder()
                        .name("Personal Loan")
                        .interestRate(new BigDecimal("0.15"))
                        .build())
                .state(State.builder()
                        .name("Pending review")
                        .build())
                .userInfo(new UserInfo("user123", "John", "Doe", "john.doe@example.com",
                        "123456789", "123 Main St", LocalDate.of(1990, 1, 1),
                        "USER", new BigDecimal("3000")))
                .totalMonthlyDebt(new BigDecimal("500"))
                .createdAt(LocalDateTime.now())
                .build();

        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(serverRequest.queryParam("page")).thenReturn(java.util.Optional.of(String.valueOf(page)));
        when(serverRequest.queryParam("size")).thenReturn(java.util.Optional.of(String.valueOf(size)));
        when(authorizationService.validateTokenAndRole(jwtToken, "Advisor")).thenReturn(Mono.empty());
        when(loanApplicationUseCase.getLoanApplicationsForReview(jwtToken, page, size))
                .thenReturn(Flux.just(review));

        // Act
        Mono<ServerResponse> result = handler.getLoanApplicationsForReview(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getLoanApplicationsForReview_insufficientPrivileges() {
        // Arrange
        String jwtToken = "valid.jwt.token";

        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(authorizationService.validateTokenAndRole(jwtToken, "Advisor"))
                .thenReturn(Mono.error(new InsufficientPrivilegesException("User does not have required role: Advisor")));

        // Act
        Mono<ServerResponse> result = handler.getLoanApplicationsForReview(serverRequest);

        // Assert - InsufficientPrivilegesException should return 403 Forbidden
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.FORBIDDEN))
                .verifyComplete();
    }

    @Test
    void getLoanApplicationsForReview_invalidJwtToken() {
        // Arrange
        String jwtToken = "invalid.jwt.token";

        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(authorizationService.validateTokenAndRole(jwtToken, "Advisor"))
                .thenReturn(Mono.error(new InvalidJwtTokenException("Invalid JWT token")));

        // Act
        Mono<ServerResponse> result = handler.getLoanApplicationsForReview(serverRequest);

        // Assert - InvalidJwtTokenException should return 401 Unauthorized
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.UNAUTHORIZED))
                .verifyComplete();
    }

    @Test
    void getLoanApplicationsForReview_missingAuthorizationHeader() {
        // Arrange
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn(null);

        // Act
        Mono<ServerResponse> result = handler.getLoanApplicationsForReview(serverRequest);

        // Assert - MissingAuthorizationHeaderException should return 401 Unauthorized
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.UNAUTHORIZED))
                .verifyComplete();
    }

    @Test
    void getLoanApplicationsForReview_defaultPagination() {
        // Arrange
        String jwtToken = "valid.jwt.token";

        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(serverRequest.queryParam("page")).thenReturn(java.util.Optional.empty());
        when(serverRequest.queryParam("size")).thenReturn(java.util.Optional.empty());
        when(authorizationService.validateTokenAndRole(jwtToken, "Advisor")).thenReturn(Mono.empty());
        when(loanApplicationUseCase.getLoanApplicationsForReview(jwtToken, 0, 10))
                .thenReturn(Flux.empty());

        // Act
        Mono<ServerResponse> result = handler.getLoanApplicationsForReview(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getLoanApplicationsForReview_paginationLimits() {
        // Arrange
        String jwtToken = "valid.jwt.token";

        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(serverRequest.queryParam("page")).thenReturn(java.util.Optional.of("-1"));
        when(serverRequest.queryParam("size")).thenReturn(java.util.Optional.of("150"));
        when(authorizationService.validateTokenAndRole(jwtToken, "Advisor")).thenReturn(Mono.empty());
        when(loanApplicationUseCase.getLoanApplicationsForReview(jwtToken, 0, 100))
                .thenReturn(Flux.empty());

        // Act
        Mono<ServerResponse> result = handler.getLoanApplicationsForReview(serverRequest);

        // Assert - Should apply limits: page=0, size=100 (max allowed)
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }
}
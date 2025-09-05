package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.user.UserInfo;
import co.com.bancolombia.model.user.UserValidator;
import co.com.bancolombia.model.exception.business.InvalidLoanAmountException;
import co.com.bancolombia.model.exception.business.LoanTypeNotFoundException;
import co.com.bancolombia.model.exception.business.StateNotFoundException;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.LoanApplicationReview;
import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.state.State;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.state.State;
import co.com.bancolombia.model.state.gateways.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class LoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private StateRepository stateRepository;

    @Mock
    private UserValidator userValidator;

    private LoanApplicationUseCase useCase;

    private static final String JWT_TOKEN = "mock.jwt.token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new LoanApplicationUseCase(loanApplicationRepository, loanTypeRepository, stateRepository, userValidator);

        // Mock UserValidator to return a UserInfo for any clientId and JWT token
        UserInfo mockUserInfo = new UserInfo(123L, "John", "Doe", "john.doe@example.com", "123456789",
                "555-1234", "123 Main St", LocalDate.of(1990, 1, 1), "USER", BigDecimal.valueOf(5000));
        when(userValidator.validateUserExists(anyString(), anyString())).thenReturn(Mono.just(mockUserInfo));

        // Mock LoanTypeRepository
        LoanType mockLoanType = LoanType.builder()
                .id(1L)
                .name("Personal Loan")
                .interestRate(BigDecimal.valueOf(0.12))
                .minAmount(BigDecimal.valueOf(1000))
                .maxAmount(BigDecimal.valueOf(50000))
                .build();
        when(loanTypeRepository.findById(anyLong())).thenReturn(Mono.just(mockLoanType));

        // Mock StateRepository
        State mockState = State.builder()
                .id(1L)
                .name("Pending review")
                .build();
        when(stateRepository.findById(anyLong())).thenReturn(Mono.just(mockState));
    }

    @Test
    void registerLoanApplication_success() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";
        BigDecimal amount = new BigDecimal("50000");
        Integer term = 12;
        Long loanTypeId = 1L;
        Long statusId = 1L;

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        LoanApplication input = LoanApplication.builder()
                .clientId(clientId)
                .amount(amount)
                .term(term)
                .loanTypeId(loanTypeId)
                .build();

        LoanType loanType = LoanType.builder()
                .id(loanTypeId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .build();

        State pendingState = State.builder()
                .id(statusId)
                .name("Pending review")
                .description("Application is pending review")
                .build();

        LoanApplication expected = LoanApplication.builder()
                .clientId(clientId)
                .amount(amount)
                .term(term)
                .loanTypeId(loanTypeId)
                .status(statusId)
                .createdAt(LocalDateTime.now())
                .build();

        when(userValidator.validateUserExists(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));
        when(stateRepository.findByName("Pending review")).thenReturn(Mono.just(pendingState));
        when(loanApplicationRepository.saveLoanApplication(any(LoanApplication.class))).thenReturn(Mono.just(expected));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectNextMatches(saved -> saved.getClientId().equals(clientId) &&
                        saved.getAmount().equals(amount) &&
                        saved.getTerm().equals(term) &&
                        saved.getLoanTypeId().equals(loanTypeId) &&
                        saved.getStatus().equals(statusId))
                .verifyComplete();
    }

    @Test
    void registerLoanApplication_loanTypeNotFound() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";
        Long loanTypeId = 999L;

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        LoanApplication input = LoanApplication.builder()
                .clientId(clientId)
                .loanTypeId(loanTypeId)
                .build();

        when(userValidator.validateUserExists(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }

    @Test
    void registerLoanApplication_amountBelowMinimum() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";
        Long loanTypeId = 1L;
        BigDecimal amount = new BigDecimal("5000"); // Below minimum

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        LoanApplication input = LoanApplication.builder()
                .clientId(clientId)
                .amount(amount)
                .loanTypeId(loanTypeId)
                .build();

        LoanType loanType = LoanType.builder()
                .id(loanTypeId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .build();

        when(userValidator.validateUserExists(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectError(InvalidLoanAmountException.class)
                .verify();
    }

    @Test
    void registerLoanApplication_amountAboveMaximum() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";
        Long loanTypeId = 1L;
        BigDecimal amount = new BigDecimal("200000"); // Above maximum

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        LoanApplication input = LoanApplication.builder()
                .clientId(clientId)
                .amount(amount)
                .loanTypeId(loanTypeId)
                .build();

        LoanType loanType = LoanType.builder()
                .id(loanTypeId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .build();

        when(userValidator.validateUserExists(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectError(InvalidLoanAmountException.class)
                .verify();
    }

    @Test
    void registerLoanApplication_pendingReviewStateNotFound() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";
        Long loanTypeId = 1L;
        BigDecimal amount = new BigDecimal("50000");

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        LoanApplication input = LoanApplication.builder()
                .clientId(clientId)
                .amount(amount)
                .loanTypeId(loanTypeId)
                .build();

        LoanType loanType = LoanType.builder()
                .id(loanTypeId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .build();

        when(userValidator.validateUserExists(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));
        when(stateRepository.findByName("Pending review")).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectError(StateNotFoundException.class)
                .verify();
    }

    @Test
    void registerLoanApplication_userNotFound() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";
        BigDecimal amount = new BigDecimal("50000");
        Long loanTypeId = 1L;

        LoanApplication input = LoanApplication.builder()
                .clientId(clientId)
                .amount(amount)
                .loanTypeId(loanTypeId)
                .build();

        when(userValidator.validateUserExists(clientId, jwtToken)).thenReturn(Mono.error(new RuntimeException("User not found - cannot create loan application")));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void registerLoanApplication_userIdMismatch() {
        // Arrange
        String jwtToken = "jwt.with.different.userId";
        String clientId = "client123";
        BigDecimal amount = new BigDecimal("50000");
        Long loanTypeId = 1L;

        LoanApplication input = LoanApplication.builder()
                .clientId(clientId)
                .amount(amount)
                .loanTypeId(loanTypeId)
                .build();

        // Mock RestConsumer to throw userId mismatch error
        when(userValidator.validateUserExists(clientId, jwtToken))
                .thenReturn(Mono.error(new RuntimeException("User ID mismatch")));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void getLoanApplicationsForReview_success() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        int page = 0;
        int size = 10;

        UUID applicationId = UUID.randomUUID();
        String clientId = "client123";
        BigDecimal amount = new BigDecimal("50000");
        Integer term = 12;
        Long loanTypeId = 1L;
        Long stateId = 1L;

        LoanApplication application = LoanApplication.builder()
                .id(applicationId)
                .clientId(clientId)
                .loanTypeId(1L)
                .status(1L)
                .build();

        // Set up mocks for this specific test
        when(loanApplicationRepository.findByStatus("Pending review", size, 0L)).thenReturn(Flux.just(application));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(LoanType.builder()
                .id(1L)
                .name("Personal Loan")
                .interestRate(BigDecimal.valueOf(0.12))
                .minAmount(BigDecimal.valueOf(1000))
                .maxAmount(BigDecimal.valueOf(50000))
                .build()));
        when(stateRepository.findById(1L)).thenReturn(Mono.just(State.builder()
                .id(1L)
                .name("Pending review")
                .build()));

         // Act & Assert
        StepVerifier.create(useCase.getLoanApplicationsForReview(jwtToken, page, size))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getLoanApplicationsForReview_emptyResult() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        int page = 0;
        int size = 10;

        when(loanApplicationRepository.findByStatus("Pending review", size, 0L)).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(useCase.getLoanApplicationsForReview(jwtToken, page, size))
                .verifyComplete();
    }

    @Test
    void getLoanApplicationsForReview_userNotFound() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        int page = 0;
        int size = 10;

        UUID applicationId = UUID.randomUUID();
        String clientId = "client123";
        Long loanTypeId = 1L;
        Long stateId = 1L;

        LoanApplication application = LoanApplication.builder()
                .id(applicationId)
                .clientId(clientId)
                .loanTypeId(loanTypeId)
                .status(stateId)
                .build();

        when(loanApplicationRepository.findByStatus("Pending review", size, 0L)).thenReturn(Flux.just(application));
        when(userValidator.validateUserExists(clientId, jwtToken)).thenReturn(Mono.error(new RuntimeException("User not found")));

        // Act & Assert
        StepVerifier.create(useCase.getLoanApplicationsForReview(jwtToken, page, size))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void getLoanApplicationsForReview_loanTypeNotFound() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        int page = 0;
        int size = 10;

        UUID applicationId = UUID.randomUUID();
        String clientId = "client123";
        Long loanTypeId = 999L;
        Long stateId = 1L;

        LoanApplication application = LoanApplication.builder()
                .id(applicationId)
                .clientId(clientId)
                .loanTypeId(loanTypeId)
                .status(stateId)
                .build();

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        when(loanApplicationRepository.findByStatus("Pending review", size, 0L)).thenReturn(Flux.just(application));
        when(userValidator.validateUserExists(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.getLoanApplicationsForReview(jwtToken, page, size))
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }

    @Test
    void getLoanApplicationsForReview_stateNotFound() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        int page = 0;
        int size = 10;

        UUID applicationId = UUID.randomUUID();
        String clientId = "client123";
        Long loanTypeId = 1L;
        Long stateId = 999L;

        LoanApplication application = LoanApplication.builder()
                .id(applicationId)
                .clientId(clientId)
                .loanTypeId(loanTypeId)
                .status(stateId)
                .build();

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        LoanType loanType = LoanType.builder()
                .id(loanTypeId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .build();

        when(loanApplicationRepository.findByStatus("Pending review", size, 0L)).thenReturn(Flux.just(application));
        when(userValidator.validateUserExists(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));
        when(stateRepository.findById(stateId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.getLoanApplicationsForReview(jwtToken, page, size))
                .expectError(StateNotFoundException.class)
                .verify();
    }

    @Test
    void getClientLoanApplications_success() {
        // Arrange
        String clientId = "client123";
        int page = 0;
        int size = 10;

        UUID applicationId = UUID.randomUUID();
        LoanApplication application = LoanApplication.builder()
                .id(applicationId)
                .clientId(clientId)
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)  // Match the mocked loan type ID
                .status(1L)      // Match the mocked state ID
                .build();

        // Set up mocks for this specific test
        when(loanApplicationRepository.findByClientId(eq(clientId), anyInt(), anyLong())).thenReturn(Flux.just(application));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(LoanType.builder()
                .id(1L)
                .name("Personal Loan")
                .interestRate(BigDecimal.valueOf(0.12))
                .minAmount(BigDecimal.valueOf(1000))
                .maxAmount(BigDecimal.valueOf(50000))
                .build()));
        when(stateRepository.findById(1L)).thenReturn(Mono.just(State.builder()
                .id(1L)
                .name("Pending review")
                .build()));

        // Act & Assert - Verify that one LoanApplicationReview is returned
        StepVerifier.create(useCase.getClientLoanApplications(clientId, page, size, JWT_TOKEN))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getClientLoanApplications_emptyResult() {
        // Arrange
        String clientId = "client123";
        int page = 0;
         int size = 10;

         when(loanApplicationRepository.findByClientId(eq(clientId), anyInt(), anyLong())).thenReturn(Flux.empty());

         // Act & Assert - Verify that no items are returned for empty result
        StepVerifier.create(useCase.getClientLoanApplications(clientId, page, size, JWT_TOKEN))
                .verifyComplete();
    }

    @Test
    void getClientLoanApplications_withPagination() {
        // Arrange
        String clientId = "client123";
        int page = 1;
        int size = 5;

        UUID applicationId1 = UUID.randomUUID();
        UUID applicationId2 = UUID.randomUUID();

        LoanApplication application1 = LoanApplication.builder()
                .id(applicationId1)
                .clientId(clientId)
                .loanTypeId(1L)
                .status(1L)
                .build();

        LoanApplication application2 = LoanApplication.builder()
                .id(applicationId2)
                .clientId(clientId)
                .loanTypeId(1L)
                .status(1L)
                .build();

        // Set up mocks for this specific test
        when(loanApplicationRepository.findByClientId(eq(clientId), anyInt(), anyLong())).thenReturn(Flux.just(application2)); // Only return second application for pagination
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(LoanType.builder()
                .id(1L)
                .name("Personal Loan")
                .interestRate(BigDecimal.valueOf(0.12))
                .minAmount(BigDecimal.valueOf(1000))
                .maxAmount(BigDecimal.valueOf(50000))
                .build()));
        when(stateRepository.findById(1L)).thenReturn(Mono.just(State.builder()
                .id(1L)
                .name("Pending review")
                .build()));

         // Act & Assert - Verify that one item is returned (second item due to pagination)
        StepVerifier.create(useCase.getClientLoanApplications(clientId, page, size, JWT_TOKEN))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void registerLoanApplication_validAmountAtBoundaries() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";
        Long loanTypeId = 1L;
        Long statusId = 1L;

        // Test minimum amount
        BigDecimal minAmount = new BigDecimal("10000");
        // Test maximum amount
        BigDecimal maxAmount = new BigDecimal("100000");

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        LoanType loanType = LoanType.builder()
                .id(loanTypeId)
                .name("Personal Loan")
                .minAmount(minAmount)
                .maxAmount(maxAmount)
                .interestRate(new BigDecimal("0.15"))
                .build();

        State pendingState = State.builder()
                .id(statusId)
                .name("Pending review")
                .build();

        // Test minimum amount
        LoanApplication minInput = LoanApplication.builder()
                .clientId(clientId)
                .amount(minAmount)
                .term(12)
                .loanTypeId(loanTypeId)
                .build();

        // Test maximum amount
        LoanApplication maxInput = LoanApplication.builder()
                .clientId(clientId)
                .amount(maxAmount)
                .term(12)
                .loanTypeId(loanTypeId)
                .build();

        when(userValidator.validateUserExists(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));
        when(stateRepository.findByName("Pending review")).thenReturn(Mono.just(pendingState));
        when(loanApplicationRepository.saveLoanApplication(any(LoanApplication.class))).thenReturn(Mono.just(minInput));

        // Act & Assert for minimum amount
        StepVerifier.create(useCase.registerLoanApplication(minInput, jwtToken))
                .expectNextMatches(saved -> saved.getAmount().equals(minAmount))
                .verifyComplete();

        // Test maximum amount
        when(loanApplicationRepository.saveLoanApplication(any(LoanApplication.class))).thenReturn(Mono.just(maxInput));

        StepVerifier.create(useCase.registerLoanApplication(maxInput, jwtToken))
                .expectNextMatches(saved -> saved.getAmount().equals(maxAmount))
                .verifyComplete();
    }

    @Test
    void registerLoanApplication_withNullValues() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";
        Long loanTypeId = 1L;
        Long statusId = 1L;

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        LoanApplication input = LoanApplication.builder()
                .clientId(clientId)
                .amount(null) // Null amount
                .term(null)   // Null term
                .loanTypeId(loanTypeId)
                .build();

        LoanType loanType = LoanType.builder()
                .id(loanTypeId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .build();

        State pendingState = State.builder()
                .id(statusId)
                .name("Pending review")
                .build();

        when(userValidator.validateUserExists(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));
        when(stateRepository.findByName("Pending review")).thenReturn(Mono.just(pendingState));
        when(loanApplicationRepository.saveLoanApplication(any(LoanApplication.class))).thenReturn(Mono.just(input));

        // Act & Assert - Should throw NullPointerException due to null amount
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectError(NullPointerException.class)
                .verify();
    }

    @Test
    void getLoanApplicationsForReview_multipleApplications() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        int page = 0;
        int size = 10;

        UUID applicationId1 = UUID.randomUUID();
        UUID applicationId2 = UUID.randomUUID();
        String clientId1 = "client123";
        String clientId2 = "client456";
        Long loanTypeId = 1L;
        Long stateId = 1L;

        LoanApplication application1 = LoanApplication.builder()
                .id(applicationId1)
                .clientId(clientId1)
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(loanTypeId)
                .status(stateId)
                .createdAt(LocalDateTime.now())
                .build();

        LoanApplication application2 = LoanApplication.builder()
                .id(applicationId2)
                .clientId(clientId2)
                .amount(new BigDecimal("75000"))
                .term(24)
                .loanTypeId(loanTypeId)
                .status(stateId)
                .createdAt(LocalDateTime.now())
                .build();

        UserInfo userInfo1 = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId1, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        UserInfo userInfo2 = new UserInfo(2L, "Jane", "Smith", "jane@example.com",
                clientId2, "0987654321", "Address 2", LocalDate.now(), "USER", new BigDecimal("60000"));

        LoanType loanType = LoanType.builder()
                .id(loanTypeId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .build();

        State state = State.builder()
                .id(stateId)
                .name("Pending review")
                .description("Application is pending review")
                .build();

        when(loanApplicationRepository.findByStatus("Pending review", size, 0L)).thenReturn(Flux.just(application1, application2));
        when(userValidator.validateUserExists(clientId1, jwtToken)).thenReturn(Mono.just(userInfo1));
        when(userValidator.validateUserExists(clientId2, jwtToken)).thenReturn(Mono.just(userInfo2));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));
        when(stateRepository.findById(stateId)).thenReturn(Mono.just(state));

        // Act & Assert
        StepVerifier.create(useCase.getLoanApplicationsForReview(jwtToken, page, size))
                .expectNextMatches(review -> review.getId().equals(applicationId1))
                .expectNextMatches(review -> review.getId().equals(applicationId2))
                .verifyComplete();
    }

    @Test
    void getClientLoanApplications_invalidPage() {
        // Arrange
        String clientId = "client123";
        int invalidPage = -1; // Invalid page
        int size = 10;

        UUID applicationId = UUID.randomUUID();
        LoanApplication application = LoanApplication.builder()
                .id(applicationId)
                .clientId(clientId)
                .loanTypeId(1L)
                .status(1L)
                .build();

        when(loanApplicationRepository.findByClientId(eq(clientId), anyInt(), anyLong())).thenReturn(Flux.just(application));
        when(userValidator.validateUserExists(eq(clientId), eq(JWT_TOKEN))).thenReturn(Mono.just(new UserInfo(123L, "John", "Doe", "john.doe@example.com", "123456789", "555-1234", "123 Main St", LocalDate.of(1990, 1, 1), "USER", BigDecimal.valueOf(5000))));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(LoanType.builder()
                .id(1L)
                .name("Personal Loan")
                .interestRate(BigDecimal.valueOf(0.12))
                .minAmount(BigDecimal.valueOf(1000))
                .maxAmount(BigDecimal.valueOf(50000))
                .build()));
        when(stateRepository.findById(1L)).thenReturn(Mono.just(State.builder()
                .id(1L)
                .name("Pending review")
                .build()));

         // Act & Assert - Should handle negative page gracefully
        StepVerifier.create(useCase.getClientLoanApplications(clientId, invalidPage, size, JWT_TOKEN))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getClientLoanApplications_largePageSize() {
        // Arrange
        String clientId = "client123";
        int page = 0;
        int largeSize = 1000; // Large page size

        UUID applicationId = UUID.randomUUID();
        LoanApplication application = LoanApplication.builder()
                .id(applicationId)
                .clientId(clientId)
                .loanTypeId(1L)
                .status(1L)
                .build();

        // Set up mocks for this specific test
        when(loanApplicationRepository.findByClientId(eq(clientId), anyInt(), anyLong())).thenReturn(Flux.just(application));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(LoanType.builder()
                .id(1L)
                .name("Personal Loan")
                .interestRate(BigDecimal.valueOf(0.12))
                .minAmount(BigDecimal.valueOf(1000))
                .maxAmount(BigDecimal.valueOf(50000))
                .build()));
        when(stateRepository.findById(1L)).thenReturn(Mono.just(State.builder()
                .id(1L)
                .name("Pending review")
                .build()));

         // Act & Assert
        StepVerifier.create(useCase.getClientLoanApplications(clientId, page, largeSize, JWT_TOKEN))
                .expectNextCount(1)
                .verifyComplete();
    }
}
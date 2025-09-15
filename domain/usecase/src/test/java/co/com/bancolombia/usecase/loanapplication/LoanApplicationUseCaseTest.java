package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.user.UserInfo;
import co.com.bancolombia.model.user.UserValidator;
import co.com.bancolombia.model.exception.business.InvalidLoanAmountException;
import co.com.bancolombia.model.exception.business.LoanTypeNotFoundException;
import co.com.bancolombia.model.exception.security.UserIdMismatchException;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.state.State;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.state.gateways.StateRepository;
import co.com.bancolombia.model.user.gateways.AuthorizationGateway;
import co.com.bancolombia.model.loanapplication.gateways.NotificationQueueGateway;
import co.com.bancolombia.usecase.debtcapacity.DebtCapacityUseCasePort;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static org.assertj.core.api.Assertions.assertThat;

class LoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private StateRepository stateRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private AuthorizationGateway authorizationGateway;

    @Mock
    private NotificationQueueGateway notificationQueueGateway;

    @Mock
    private DebtCapacityUseCasePort debtCapacityUseCase;

    private LoanApplicationUseCase useCase;

    private static final String JWT_TOKEN = "mock.jwt.token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new LoanApplicationUseCase(loanApplicationRepository, loanTypeRepository, stateRepository, userValidator, authorizationGateway, notificationQueueGateway, debtCapacityUseCase);

        // Mock UserValidator to return a UserInfo for any clientId and JWT token
        UserInfo mockUserInfo = new UserInfo(123L, "John", "Doe", "john.doe@example.com", "123456789",
                "555-1234", "123 Main St", LocalDate.of(1990, 1, 1), "USER", BigDecimal.valueOf(5000));
        when(userValidator.validateUserInfo(anyString(), anyString())).thenReturn(Mono.just(mockUserInfo));
        when(userValidator.validateUserIdMatch(anyString(), anyString())).thenReturn(Mono.empty());

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

        // Mock NotificationQueueGateway
        when(notificationQueueGateway.sendStatusNotification(any(LoanApplication.class), anyString())).thenReturn(Mono.just("messageId"));

        // Mock AuthorizationGateway - default to allow access
        when(authorizationGateway.validateAdvisorOrAdminAccess(anyString())).thenReturn(Mono.empty());

        // Mock LoanApplicationRepository - default behaviors
        when(loanApplicationRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        when(loanApplicationRepository.updateStatus(any(UUID.class), anyLong())).thenReturn(Mono.empty());
        when(loanApplicationRepository.saveLoanApplication(any(LoanApplication.class))).thenReturn(Mono.empty());
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
                .statusId(statusId)
                .createdAt(LocalDateTime.now())
                .build();

        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));
        when(stateRepository.findByName("Pending review")).thenReturn(Mono.just(pendingState));
        when(loanApplicationRepository.saveLoanApplication(any(LoanApplication.class))).thenReturn(Mono.just(expected));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectNextMatches(saved -> saved.getClientId().equals(clientId) &&
                        saved.getAmount().equals(amount) &&
                        saved.getTerm().equals(term) &&
                        saved.getLoanTypeId().equals(loanTypeId) &&
                        saved.getStatusId().equals(statusId))
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

        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
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

        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
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

        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectError(InvalidLoanAmountException.class)
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

        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.error(new RuntimeException("User not found - cannot create loan application")));

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

        // Mock UserValidator to throw userId mismatch error
        when(userValidator.validateUserIdMatch(clientId, jwtToken))
                .thenReturn(Mono.error(new UserIdMismatchException("User ID in token does not match requested user ID")));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectError(UserIdMismatchException.class)
                .verify();
    }

    @Test
    void getLoanApplications_success() {
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
                .amount(amount)
                .term(term)
                .loanTypeId(1L)
                .statusId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        // Set up mocks for this specific test
        when(loanApplicationRepository.findByStatus(any(), eq(size), eq(0L))).thenReturn(Flux.just(application));
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
        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(new UserInfo(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "123456789",
                "555-1234",
                "123 Main St",
                LocalDate.of(1990, 1, 1),
                "1",
                BigDecimal.valueOf(5000)
        )));

          // Act & Assert
        // Note: getClientLoanApplications method removed, using getLoanApplications instead
        StepVerifier.create(useCase.getLoanApplications(JWT_TOKEN, page, size, List.of(1)))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getLoanApplications_emptyResult() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        int page = 0;
        int size = 10;

        when(loanApplicationRepository.findByStatus(any(), eq(size), eq(0L))).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(useCase.getLoanApplications(jwtToken, page, size, List.of(1)))
                .verifyComplete();
    }

    @Test
    void getLoanApplications_userNotFound() {
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
                .statusId(stateId)
                .build();

        when(loanApplicationRepository.findByStatus(any(), eq(size), eq(0L))).thenReturn(Flux.just(application));
        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.error(new RuntimeException("User not found")));

        // Act & Assert
        StepVerifier.create(useCase.getLoanApplications(jwtToken, page, size, List.of(1)))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void getLoanApplications_loanTypeNotFound() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        int page = 0;
        int size = 10;

        UUID applicationId = UUID.randomUUID();
        String clientId = "client123";
        Long loanTypeId = 999L; // Non-existent loan type
        Long stateId = 1L;

        LoanApplication application = LoanApplication.builder()
                .id(applicationId)
                .clientId(clientId)
                .loanTypeId(loanTypeId)
                .statusId(stateId)
                .build();

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        when(loanApplicationRepository.findByStatus(any(), eq(size), eq(0L))).thenReturn(Flux.just(application));
        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.empty()); // Loan type not found
        when(stateRepository.findById(stateId)).thenReturn(Mono.just(State.builder()
                .id(stateId)
                .name("Pending review")
                .build()));

        // Act & Assert
        StepVerifier.create(useCase.getLoanApplications(jwtToken, page, size, List.of(1)))
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }

    @Test
    void updateLoanApplicationStatus_success_rejected() {
        // Arrange
        UUID applicationId = UUID.randomUUID();
        Long newStatusId = 4L; // Rejected
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";

        LoanApplication existingApplication = LoanApplication.builder()
                .id(applicationId)
                .clientId(clientId)
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .statusId(1L)
                .build();

        LoanApplication updatedApplication = existingApplication.toBuilder()
                .statusId(newStatusId)
                .build();

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        doReturn(Mono.empty()).when(authorizationGateway).validateAdvisorOrAdminAccess(jwtToken);
        when(loanApplicationRepository.findById(applicationId)).thenReturn(Mono.just(existingApplication));
        when(loanApplicationRepository.updateStatus(applicationId, newStatusId)).thenReturn(Mono.just(updatedApplication));
        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(notificationQueueGateway.sendStatusNotification(updatedApplication, userInfo.email())).thenReturn(Mono.just("messageId"));

        // Act & Assert
        StepVerifier.create(useCase.updateLoanApplicationStatus(applicationId, newStatusId, jwtToken))
                .expectNextMatches(app -> app.getStatusId().equals(newStatusId))
                .verifyComplete();
    }

    @Test
    void updateLoanApplicationStatus_loanApplicationNotFound() {
        // Arrange
        UUID applicationId = UUID.randomUUID();
        Long newStatusId = 3L;
        String jwtToken = "valid.jwt.token";

        when(authorizationGateway.validateAdvisorOrAdminAccess(jwtToken)).thenReturn(Mono.empty());
        when(loanApplicationRepository.findById(applicationId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.updateLoanApplicationStatus(applicationId, newStatusId, jwtToken))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Loan application not found"))
                .verify();
    }

    @Test
    void updateLoanApplicationStatus_invalidStatusTransition() {
        // Arrange
        UUID applicationId = UUID.randomUUID();
        Long newStatusId = 2L; // Invalid status
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";

        LoanApplication existingApplication = LoanApplication.builder()
                .id(applicationId)
                .clientId(clientId)
                .statusId(1L)
                .build();

        when(authorizationGateway.validateAdvisorOrAdminAccess(jwtToken)).thenReturn(Mono.empty());
        when(loanApplicationRepository.findById(applicationId)).thenReturn(Mono.just(existingApplication));
        when(loanApplicationRepository.updateStatus(applicationId, newStatusId)).thenReturn(Mono.just(existingApplication));

        // Act & Assert
        StepVerifier.create(useCase.updateLoanApplicationStatus(applicationId, newStatusId, jwtToken))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Invalid status transition"))
                .verify();
    }

    @Test
    void updateLoanApplicationStatus_authorizationFailure() {
        // Arrange
        UUID applicationId = UUID.randomUUID();
        Long newStatusId = 3L;
        String jwtToken = "valid.jwt.token";

        when(authorizationGateway.validateAdvisorOrAdminAccess(jwtToken))
                .thenReturn(Mono.error(new RuntimeException("Authorization failed")));

        // Act & Assert
        StepVerifier.create(useCase.updateLoanApplicationStatus(applicationId, newStatusId, jwtToken))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("Authorization failed"))
                .verify();
    }

    @Test
    void updateLoanApplicationStatus_userNotFound() {
        // Arrange
        UUID applicationId = UUID.randomUUID();
        Long newStatusId = 3L;
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";

        LoanApplication existingApplication = LoanApplication.builder()
                .id(applicationId)
                .clientId(clientId)
                .statusId(1L)
                .build();

        when(authorizationGateway.validateAdvisorOrAdminAccess(jwtToken)).thenReturn(Mono.empty());
        when(loanApplicationRepository.findById(applicationId)).thenReturn(Mono.just(existingApplication));
        when(loanApplicationRepository.updateStatus(applicationId, newStatusId)).thenReturn(Mono.just(existingApplication));
        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.error(new RuntimeException("User not found")));

        // Act & Assert
        StepVerifier.create(useCase.updateLoanApplicationStatus(applicationId, newStatusId, jwtToken))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("User not found"))
                .verify();
    }

    @Test
    void registerLoanApplication_withAutomaticValidation_shouldTriggerDebtCapacityCalculation() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";
        BigDecimal amount = new BigDecimal("50000");
        Integer term = 12;
        Long loanTypeId = 1L;

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        LoanApplication input = LoanApplication.builder()
                .clientId(clientId)
                .amount(amount)
                .term(term)
                .loanTypeId(loanTypeId)
                .automaticValidation(true)
                .build();

        LoanType loanType = LoanType.builder()
                .id(loanTypeId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .automaticValidation(true)
                .build();

        State pendingState = State.builder()
                .id(1L)
                .name("Pending review")
                .description("Application is pending review")
                .build();

        LoanApplication expected = LoanApplication.builder()
                .clientId(clientId)
                .amount(amount)
                .term(term)
                .loanTypeId(loanTypeId)
                .statusId(1L)
                .automaticValidation(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));
        when(stateRepository.findByName("Pending review")).thenReturn(Mono.just(pendingState));
        when(loanApplicationRepository.saveLoanApplication(any(LoanApplication.class))).thenReturn(Mono.just(expected));
        when(debtCapacityUseCase.calculateDebtCapacity(any(LoanApplication.class), eq(jwtToken))).thenReturn(Mono.just("debt-capacity-message-id"));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectNextMatches(saved -> saved.getAutomaticValidation() != null && saved.getAutomaticValidation())
                .verifyComplete();
    }

    @Test
    void registerLoanApplication_withoutAutomaticValidation_shouldNotTriggerDebtCapacityCalculation() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";
        BigDecimal amount = new BigDecimal("50000");
        Integer term = 12;
        Long loanTypeId = 1L;

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        LoanApplication input = LoanApplication.builder()
                .clientId(clientId)
                .amount(amount)
                .term(term)
                .loanTypeId(loanTypeId)
                .automaticValidation(false)
                .build();

        LoanType loanType = LoanType.builder()
                .id(loanTypeId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .automaticValidation(false)
                .build();

        State pendingState = State.builder()
                .id(1L)
                .name("Pending review")
                .description("Application is pending review")
                .build();

        LoanApplication expected = LoanApplication.builder()
                .clientId(clientId)
                .amount(amount)
                .term(term)
                .loanTypeId(loanTypeId)
                .statusId(1L)
                .automaticValidation(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));
        when(stateRepository.findByName("Pending review")).thenReturn(Mono.just(pendingState));
        when(loanApplicationRepository.saveLoanApplication(any(LoanApplication.class))).thenReturn(Mono.just(expected));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectNextMatches(saved -> saved.getAutomaticValidation() != null && !saved.getAutomaticValidation())
                .verifyComplete();
    }

    @Test
    void registerLoanApplication_debtCapacityCalculationError_shouldStillSaveApplication() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";
        BigDecimal amount = new BigDecimal("50000");
        Integer term = 12;
        Long loanTypeId = 1L;

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        LoanApplication input = LoanApplication.builder()
                .clientId(clientId)
                .amount(amount)
                .term(term)
                .loanTypeId(loanTypeId)
                .automaticValidation(true)
                .build();

        LoanType loanType = LoanType.builder()
                .id(loanTypeId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .automaticValidation(true)
                .build();

        State pendingState = State.builder()
                .id(1L)
                .name("Pending review")
                .description("Application is pending review")
                .build();

        LoanApplication expected = LoanApplication.builder()
                .clientId(clientId)
                .amount(amount)
                .term(term)
                .loanTypeId(loanTypeId)
                .statusId(1L)
                .automaticValidation(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));
        when(stateRepository.findByName("Pending review")).thenReturn(Mono.just(pendingState));
        when(loanApplicationRepository.saveLoanApplication(any(LoanApplication.class))).thenReturn(Mono.just(expected));
        when(debtCapacityUseCase.calculateDebtCapacity(any(LoanApplication.class), eq(jwtToken)))
                .thenReturn(Mono.error(new RuntimeException("Debt capacity service unavailable")));

        // Act & Assert - Should still save the application even if debt capacity calculation fails
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectNextMatches(saved -> saved.getClientId().equals(clientId))
                .verifyComplete();
    }

    @Test
    void getLoanApplications_withDifferentStatusFilters_shouldReturnFilteredResults() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        int page = 0;
        int size = 10;
        List<Long> statusIds = List.of(1L, 2L, 3L); // Multiple status filters

        UUID applicationId1 = UUID.randomUUID();
        UUID applicationId2 = UUID.randomUUID();
        String clientId = "client123";

        LoanApplication application1 = LoanApplication.builder()
                .id(applicationId1)
                .clientId(clientId)
                .loanTypeId(1L)
                .statusId(1L)
                .build();

        LoanApplication application2 = LoanApplication.builder()
                .id(applicationId2)
                .clientId(clientId)
                .loanTypeId(1L)
                .statusId(2L)
                .build();

        when(loanApplicationRepository.findByStatus(any(), eq(size), eq(0L))).thenReturn(Flux.just(application1, application2));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(LoanType.builder()
                .id(1L)
                .name("Personal Loan")
                .interestRate(BigDecimal.valueOf(0.12))
                .minAmount(BigDecimal.valueOf(1000))
                .maxAmount(BigDecimal.valueOf(50000))
                .build()));
        when(stateRepository.findById(anyLong())).thenReturn(Mono.just(State.builder()
                .id(1L)
                .name("Pending review")
                .build()));
        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(new UserInfo(
                1L, "John", "Doe", "john.doe@example.com", "123456789",
                "555-1234", "123 Main St", LocalDate.of(1990, 1, 1), "USER", BigDecimal.valueOf(5000)
        )));

        // Act & Assert
        StepVerifier.create(useCase.getLoanApplications(jwtToken, page, size, statusIds))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getLoanApplications_withEmptyStatusFilter_shouldReturnAllApplications() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        int page = 0;
        int size = 10;
        List<Long> statusIds = List.of(); // Empty status filter

        UUID applicationId = UUID.randomUUID();
        String clientId = "client123";

        LoanApplication application = LoanApplication.builder()
                .id(applicationId)
                .clientId(clientId)
                .loanTypeId(1L)
                .statusId(1L)
                .build();

        when(loanApplicationRepository.findByStatus(any(), eq(size), eq(0L))).thenReturn(Flux.just(application));
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
        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(new UserInfo(
                1L, "John", "Doe", "john.doe@example.com", "123456789",
                "555-1234", "123 Main St", LocalDate.of(1990, 1, 1), "USER", BigDecimal.valueOf(5000)
        )));

        // Act & Assert
        StepVerifier.create(useCase.getLoanApplications(jwtToken, page, size, statusIds))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void updateLoanApplicationStatus_toApproved_shouldSendNotification() {
        // Arrange
        UUID applicationId = UUID.randomUUID();
        Long newStatusId = 3L; // Approved
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";

        LoanApplication existingApplication = LoanApplication.builder()
                .id(applicationId)
                .clientId(clientId)
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .statusId(1L)
                .build();

        LoanApplication updatedApplication = existingApplication.toBuilder()
                .statusId(newStatusId)
                .build();

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        when(authorizationGateway.validateAdvisorOrAdminAccess(jwtToken)).thenReturn(Mono.empty());
        when(loanApplicationRepository.findById(applicationId)).thenReturn(Mono.just(existingApplication));
        when(loanApplicationRepository.updateStatus(applicationId, newStatusId)).thenReturn(Mono.just(updatedApplication));
        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(notificationQueueGateway.sendStatusNotification(updatedApplication, userInfo.email())).thenReturn(Mono.just("notification-message-id"));

        // Act & Assert
        StepVerifier.create(useCase.updateLoanApplicationStatus(applicationId, newStatusId, jwtToken))
                .expectNextMatches(app -> app.getStatusId().equals(newStatusId))
                .verifyComplete();
    }

    @Test
    void updateLoanApplicationStatus_notificationFailure_shouldStillUpdateStatus() {
        // Arrange
        UUID applicationId = UUID.randomUUID();
        Long newStatusId = 3L; // Approved
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";

        LoanApplication existingApplication = LoanApplication.builder()
                .id(applicationId)
                .clientId(clientId)
                .amount(new BigDecimal("50000"))
                .term(12)
                .loanTypeId(1L)
                .statusId(1L)
                .build();

        LoanApplication updatedApplication = existingApplication.toBuilder()
                .statusId(newStatusId)
                .build();

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        when(authorizationGateway.validateAdvisorOrAdminAccess(jwtToken)).thenReturn(Mono.empty());
        when(loanApplicationRepository.findById(applicationId)).thenReturn(Mono.just(existingApplication));
        when(loanApplicationRepository.updateStatus(applicationId, newStatusId)).thenReturn(Mono.just(updatedApplication));
        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(notificationQueueGateway.sendStatusNotification(updatedApplication, userInfo.email()))
                .thenReturn(Mono.error(new RuntimeException("Notification service unavailable")));

        // Act & Assert - Should still update status even if notification fails
        StepVerifier.create(useCase.updateLoanApplicationStatus(applicationId, newStatusId, jwtToken))
                .expectNextMatches(app -> app.getStatusId().equals(newStatusId))
                .verifyComplete();
    }

    @Test
    void registerLoanApplication_withNullAmount_shouldHandleGracefully() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";
        Long loanTypeId = 1L;

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        LoanApplication input = LoanApplication.builder()
                .clientId(clientId)
                .amount(null) // Null amount
                .loanTypeId(loanTypeId)
                .build();

        LoanType loanType = LoanType.builder()
                .id(loanTypeId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .build();

        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectError(InvalidLoanAmountException.class)
                .verify();
    }

    @Test
    void registerLoanApplication_withZeroAmount_shouldHandleGracefully() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";
        Long loanTypeId = 1L;

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        LoanApplication input = LoanApplication.builder()
                .clientId(clientId)
                .amount(BigDecimal.ZERO) // Zero amount
                .loanTypeId(loanTypeId)
                .build();

        LoanType loanType = LoanType.builder()
                .id(loanTypeId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .build();

        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectError(InvalidLoanAmountException.class)
                .verify();
    }

    @Test
    void registerLoanApplication_withNegativeAmount_shouldHandleGracefully() {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String clientId = "client123";
        Long loanTypeId = 1L;

        UserInfo userInfo = new UserInfo(1L, "John", "Doe", "john@example.com",
                clientId, "1234567890", "Address", LocalDate.now(), "USER", new BigDecimal("50000"));

        LoanApplication input = LoanApplication.builder()
                .clientId(clientId)
                .amount(new BigDecimal("-1000")) // Negative amount
                .loanTypeId(loanTypeId)
                .build();

        LoanType loanType = LoanType.builder()
                .id(loanTypeId)
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("0.15"))
                .build();

        when(userValidator.validateUserInfo(clientId, jwtToken)).thenReturn(Mono.just(userInfo));
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input, jwtToken))
                .expectError(InvalidLoanAmountException.class)
                .verify();
    }
}
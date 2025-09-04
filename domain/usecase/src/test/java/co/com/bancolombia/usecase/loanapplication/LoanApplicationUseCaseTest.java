package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.user.UserInfo;
import co.com.bancolombia.model.user.UserValidator;
import co.com.bancolombia.model.exception.business.InvalidLoanAmountException;
import co.com.bancolombia.model.exception.business.LoanTypeNotFoundException;
import co.com.bancolombia.model.exception.business.StateNotFoundException;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.state.State;
import co.com.bancolombia.model.state.gateways.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new LoanApplicationUseCase(loanApplicationRepository, loanTypeRepository, stateRepository, userValidator);
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
}
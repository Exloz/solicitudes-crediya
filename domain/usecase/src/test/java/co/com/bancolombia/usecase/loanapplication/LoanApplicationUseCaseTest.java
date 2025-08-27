package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.exception.InvalidLoanAmountException;
import co.com.bancolombia.model.exception.LoanTypeNotFoundException;
import co.com.bancolombia.model.exception.StateNotFoundException;
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
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private StateRepository stateRepository;

    private LoanApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new LoanApplicationUseCase(loanApplicationRepository, loanTypeRepository, stateRepository);
    }

    @Test
    void registerLoanApplication_success() {
        // Arrange
        UUID loanId = UUID.randomUUID();
        String clientId = "client123";
        BigDecimal amount = new BigDecimal("50000");
        Integer term = 12;
        Long loanTypeId = 1L;
        Long statusId = 1L;

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

        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));
        when(stateRepository.findByName("Pending review")).thenReturn(Mono.just(pendingState));
        when(loanApplicationRepository.saveLoanApplication(any(LoanApplication.class))).thenReturn(Mono.just(expected));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input))
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
        Long loanTypeId = 999L;
        LoanApplication input = LoanApplication.builder()
                .loanTypeId(loanTypeId)
                .build();

        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input))
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }

    @Test
    void registerLoanApplication_amountBelowMinimum() {
        // Arrange
        Long loanTypeId = 1L;
        BigDecimal amount = new BigDecimal("5000"); // Below minimum

        LoanApplication input = LoanApplication.builder()
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

        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input))
                .expectError(InvalidLoanAmountException.class)
                .verify();
    }

    @Test
    void registerLoanApplication_amountAboveMaximum() {
        // Arrange
        Long loanTypeId = 1L;
        BigDecimal amount = new BigDecimal("200000"); // Above maximum

        LoanApplication input = LoanApplication.builder()
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

        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input))
                .expectError(InvalidLoanAmountException.class)
                .verify();
    }

    @Test
    void registerLoanApplication_pendingReviewStateNotFound() {
        // Arrange
        Long loanTypeId = 1L;
        BigDecimal amount = new BigDecimal("50000");

        LoanApplication input = LoanApplication.builder()
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

        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));
        when(stateRepository.findByName("Pending review")).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.registerLoanApplication(input))
                .expectError(StateNotFoundException.class)
                .verify();
    }
}
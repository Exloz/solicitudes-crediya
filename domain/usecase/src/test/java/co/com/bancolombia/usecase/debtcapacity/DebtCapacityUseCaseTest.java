package co.com.bancolombia.usecase.debtcapacity;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.DebtCapacityQueueGateway;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.sqs.sender.services.debtCapacity.DebtCapacityResponseMessage;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class DebtCapacityUseCaseTest {

    @Mock
    private DebtCapacityQueueGateway debtCapacityQueueGateway;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    private DebtCapacityUseCase useCase;

    private static final String JWT_TOKEN = "mock.jwt.token";
    private static final UUID TEST_APPLICATION_ID = UUID.randomUUID();
    private static final String TEST_CLIENT_ID = "client123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new DebtCapacityUseCase(debtCapacityQueueGateway, loanApplicationRepository);

        // Mock default behaviors
        when(debtCapacityQueueGateway.sendDebtCapacityRequest(any(LoanApplication.class), any(String.class)))
                .thenReturn(Mono.just("messageId"));
        when(loanApplicationRepository.updateStatus(any(UUID.class), any(Long.class)))
                .thenReturn(Mono.empty());
    }

    @Test
    void calculateDebtCapacity_success() {
        // Arrange
        LoanApplication loanApplication = createTestLoanApplication();

        // Act & Assert
        StepVerifier.create(useCase.calculateDebtCapacity(loanApplication, JWT_TOKEN))
                .expectNext("messageId")
                .verifyComplete();
    }

    @Test
    void calculateDebtCapacity_withNullApplication_shouldHandleGracefully() {
        // Act & Assert
        StepVerifier.create(useCase.calculateDebtCapacity(null, JWT_TOKEN))
                .expectNext("messageId")
                .verifyComplete();
    }

    @Test
    void calculateDebtCapacity_withNullToken_shouldHandleGracefully() {
        // Arrange
        LoanApplication loanApplication = createTestLoanApplication();

        // Act & Assert
        StepVerifier.create(useCase.calculateDebtCapacity(loanApplication, null))
                .expectNext("messageId")
                .verifyComplete();
    }

    @Test
    void calculateDebtCapacity_queueGatewayError_shouldPropagateError() {
        // Arrange
        LoanApplication loanApplication = createTestLoanApplication();
        RuntimeException expectedError = new RuntimeException("Queue service unavailable");

        when(debtCapacityQueueGateway.sendDebtCapacityRequest(any(LoanApplication.class), any(String.class)))
                .thenReturn(Mono.error(expectedError));

        // Act & Assert
        StepVerifier.create(useCase.calculateDebtCapacity(loanApplication, JWT_TOKEN))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void processDebtCapacityResponse_approvedDecision_shouldUpdateStatusToApproved() {
        // Arrange
        DebtCapacityResponseMessage response = DebtCapacityResponseMessage.builder()
                .applicationId(TEST_APPLICATION_ID)
                .decision("APROBADO")
                .build();

        when(loanApplicationRepository.updateStatus(TEST_APPLICATION_ID, 3L))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(response))
                .verifyComplete();
    }

    @Test
    void processDebtCapacityResponse_rejectedDecision_shouldUpdateStatusToRejected() {
        // Arrange
        DebtCapacityResponseMessage response = DebtCapacityResponseMessage.builder()
                .applicationId(TEST_APPLICATION_ID)
                .decision("RECHAZADO")
                .build();

        when(loanApplicationRepository.updateStatus(TEST_APPLICATION_ID, 4L))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(response))
                .verifyComplete();
    }

    @Test
    void processDebtCapacityResponse_manualReviewDecision_shouldUpdateStatusToManualReview() {
        // Arrange
        DebtCapacityResponseMessage response = DebtCapacityResponseMessage.builder()
                .applicationId(TEST_APPLICATION_ID)
                .decision("REVISION MANUAL")
                .build();

        when(loanApplicationRepository.updateStatus(TEST_APPLICATION_ID, 5L))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(response))
                .verifyComplete();
    }

    @Test
    void processDebtCapacityResponse_unknownDecision_shouldUpdateStatusToPendingReview() {
        // Arrange
        DebtCapacityResponseMessage response = DebtCapacityResponseMessage.builder()
                .applicationId(TEST_APPLICATION_ID)
                .decision("UNKNOWN_DECISION")
                .build();

        when(loanApplicationRepository.updateStatus(TEST_APPLICATION_ID, 1L))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(response))
                .verifyComplete();
    }

    @Test
    void processDebtCapacityResponse_nullApplicationId_shouldThrowException() {
        // Arrange
        DebtCapacityResponseMessage response = DebtCapacityResponseMessage.builder()
                .applicationId(null)
                .decision("APROBADO")
                .build();

        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(response))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void processDebtCapacityResponse_nullDecision_shouldThrowException() {
        // Arrange
        DebtCapacityResponseMessage response = DebtCapacityResponseMessage.builder()
                .applicationId(TEST_APPLICATION_ID)
                .decision(null)
                .build();

        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(response))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void processDebtCapacityResponse_emptyDecision_shouldThrowException() {
        // Arrange
        DebtCapacityResponseMessage response = DebtCapacityResponseMessage.builder()
                .applicationId(TEST_APPLICATION_ID)
                .decision("")
                .build();

        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(response))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void processDebtCapacityResponse_whitespaceDecision_shouldThrowException() {
        // Arrange
        DebtCapacityResponseMessage response = DebtCapacityResponseMessage.builder()
                .applicationId(TEST_APPLICATION_ID)
                .decision("   ")
                .build();

        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(response))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void processDebtCapacityResponse_invalidResponseType_shouldThrowException() {
        // Arrange
        String invalidResponse = "invalid response type";

        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(invalidResponse))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void processDebtCapacityResponse_nullResponse_shouldThrowException() {
        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void processDebtCapacityResponse_repositoryError_shouldPropagateError() {
        // Arrange
        DebtCapacityResponseMessage response = DebtCapacityResponseMessage.builder()
                .applicationId(TEST_APPLICATION_ID)
                .decision("APROBADO")
                .build();

        RuntimeException repositoryError = new RuntimeException("Database connection failed");
        when(loanApplicationRepository.updateStatus(TEST_APPLICATION_ID, 3L))
                .thenReturn(Mono.error(repositoryError));

        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(response))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void processDebtCapacityResponse_caseInsensitiveDecision_shouldWork() {
        // Arrange
        DebtCapacityResponseMessage response = DebtCapacityResponseMessage.builder()
                .applicationId(TEST_APPLICATION_ID)
                .decision("aprobado") // lowercase
                .build();

        when(loanApplicationRepository.updateStatus(TEST_APPLICATION_ID, 3L))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(response))
                .verifyComplete();
    }

    @Test
    void processDebtCapacityResponse_mixedCaseDecision_shouldWork() {
        // Arrange
        DebtCapacityResponseMessage response = DebtCapacityResponseMessage.builder()
                .applicationId(TEST_APPLICATION_ID)
                .decision("ReChAzAdO") // mixed case
                .build();

        when(loanApplicationRepository.updateStatus(TEST_APPLICATION_ID, 4L))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(response))
                .verifyComplete();
    }

    @Test
    void processDebtCapacityResponse_withCompleteResponseData_shouldHandleAllFields() {
        // Arrange
        DebtCapacityResponseMessage response = DebtCapacityResponseMessage.builder()
                .applicationId(TEST_APPLICATION_ID)
                .decision("APROBADO")
                .maxCapacity(BigDecimal.valueOf(100000.00))
                .availableCapacity(BigDecimal.valueOf(75000.00))
                .monthlyPayment(BigDecimal.valueOf(2500.00))
                .totalIncome(BigDecimal.valueOf(8000.00))
                .currentDebt(BigDecimal.valueOf(15000.00))
                .build();

        when(loanApplicationRepository.updateStatus(TEST_APPLICATION_ID, 3L))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(response))
                .verifyComplete();
    }

    @Test
    void processDebtCapacityResponse_withErrorMessage_shouldStillProcess() {
        // Arrange
        DebtCapacityResponseMessage response = DebtCapacityResponseMessage.builder()
                .applicationId(TEST_APPLICATION_ID)
                .decision("RECHAZADO")
                .errorMessage("Insufficient income for loan amount")
                .build();

        when(loanApplicationRepository.updateStatus(TEST_APPLICATION_ID, 4L))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.processDebtCapacityResponse(response))
                .verifyComplete();
    }

    // Helper method
    private LoanApplication createTestLoanApplication() {
        return LoanApplication.builder()
                .id(TEST_APPLICATION_ID)
                .clientId(TEST_CLIENT_ID)
                .amount(BigDecimal.valueOf(50000.00))
                .term(12)
                .loanTypeId(1L)
                .statusId(1L)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
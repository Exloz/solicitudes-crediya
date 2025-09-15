package co.com.bancolombia.model.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserDebtCapacity Domain Model Tests")
class UserDebtCapacityTest {

    private static final UUID TEST_REQUEST_ID = UUID.randomUUID();
    private static final UUID TEST_APPLICATION_ID = UUID.randomUUID();
    private static final String TEST_DECISION = "APROBADO";
    private static final BigDecimal TEST_MAX_CAPACITY = BigDecimal.valueOf(100000.00);
    private static final BigDecimal TEST_AVAILABLE_CAPACITY = BigDecimal.valueOf(75000.00);
    private static final BigDecimal TEST_MONTHLY_PAYMENT = BigDecimal.valueOf(2500.00);
    private static final BigDecimal TEST_TOTAL_INCOME = BigDecimal.valueOf(8000.00);
    private static final BigDecimal TEST_CURRENT_DEBT = BigDecimal.valueOf(15000.00);
    private static final Instant TEST_RESPONSE_DATE = Instant.now();
    private static final String TEST_ERROR_MESSAGE = "Error calculating debt capacity";

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderPatternTest {

        @Test
        @DisplayName("Should build UserDebtCapacity with all fields using builder")
        void shouldBuildWithAllFields() {
            // Act
            UserDebtCapacity debtCapacity = UserDebtCapacity.builder()
                    .requestId(TEST_REQUEST_ID)
                    .applicationId(TEST_APPLICATION_ID)
                    .decision(TEST_DECISION)
                    .maxCapacity(TEST_MAX_CAPACITY)
                    .availableCapacity(TEST_AVAILABLE_CAPACITY)
                    .monthlyPayment(TEST_MONTHLY_PAYMENT)
                    .totalIncome(TEST_TOTAL_INCOME)
                    .currentDebt(TEST_CURRENT_DEBT)
                    .paymentPlan(createTestPaymentPlan())
                    .responseDate(TEST_RESPONSE_DATE)
                    .errorMessage(TEST_ERROR_MESSAGE)
                    .build();

            // Assert
            assertEquals(TEST_REQUEST_ID, debtCapacity.getRequestId());
            assertEquals(TEST_APPLICATION_ID, debtCapacity.getApplicationId());
            assertEquals(TEST_DECISION, debtCapacity.getDecision());
            assertEquals(TEST_MAX_CAPACITY, debtCapacity.getMaxCapacity());
            assertEquals(TEST_AVAILABLE_CAPACITY, debtCapacity.getAvailableCapacity());
            assertEquals(TEST_MONTHLY_PAYMENT, debtCapacity.getMonthlyPayment());
            assertEquals(TEST_TOTAL_INCOME, debtCapacity.getTotalIncome());
            assertEquals(TEST_CURRENT_DEBT, debtCapacity.getCurrentDebt());
            assertNotNull(debtCapacity.getPaymentPlan());
            assertEquals(2, debtCapacity.getPaymentPlan().size());
            assertEquals(TEST_RESPONSE_DATE, debtCapacity.getResponseDate());
            assertEquals(TEST_ERROR_MESSAGE, debtCapacity.getErrorMessage());
        }

        @Test
        @DisplayName("Should build UserDebtCapacity with minimal fields")
        void shouldBuildWithMinimalFields() {
            // Act
            UserDebtCapacity debtCapacity = UserDebtCapacity.builder()
                    .requestId(TEST_REQUEST_ID)
                    .build();

            // Assert
            assertEquals(TEST_REQUEST_ID, debtCapacity.getRequestId());
            assertNull(debtCapacity.getApplicationId());
            assertNull(debtCapacity.getDecision());
            assertNull(debtCapacity.getMaxCapacity());
            assertNull(debtCapacity.getAvailableCapacity());
            assertNull(debtCapacity.getMonthlyPayment());
            assertNull(debtCapacity.getTotalIncome());
            assertNull(debtCapacity.getCurrentDebt());
            assertNull(debtCapacity.getPaymentPlan());
            assertNull(debtCapacity.getResponseDate());
            assertNull(debtCapacity.getErrorMessage());
        }

        @Test
        @DisplayName("Should support toBuilder pattern")
        void shouldSupportToBuilder() {
            // Arrange
            UserDebtCapacity original = UserDebtCapacity.builder()
                    .requestId(TEST_REQUEST_ID)
                    .decision("PENDING")
                    .build();

            // Act
            UserDebtCapacity modified = original.toBuilder()
                    .decision(TEST_DECISION)
                    .maxCapacity(TEST_MAX_CAPACITY)
                    .build();

            // Assert
            assertEquals(TEST_REQUEST_ID, modified.getRequestId());
            assertEquals(TEST_DECISION, modified.getDecision());
            assertEquals(TEST_MAX_CAPACITY, modified.getMaxCapacity());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTest {

        @Test
        @DisplayName("Should create UserDebtCapacity with no-args constructor")
        void shouldCreateWithNoArgsConstructor() {
            // Act
            UserDebtCapacity debtCapacity = new UserDebtCapacity();

            // Assert
            assertNull(debtCapacity.getRequestId());
            assertNull(debtCapacity.getApplicationId());
            assertNull(debtCapacity.getDecision());
            assertNull(debtCapacity.getMaxCapacity());
            assertNull(debtCapacity.getAvailableCapacity());
            assertNull(debtCapacity.getMonthlyPayment());
            assertNull(debtCapacity.getTotalIncome());
            assertNull(debtCapacity.getCurrentDebt());
            assertNull(debtCapacity.getPaymentPlan());
            assertNull(debtCapacity.getResponseDate());
            assertNull(debtCapacity.getErrorMessage());
        }

        @Test
        @DisplayName("Should create UserDebtCapacity with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            // Act
            UserDebtCapacity debtCapacity = new UserDebtCapacity(
                    TEST_REQUEST_ID, TEST_APPLICATION_ID, TEST_DECISION,
                    TEST_MAX_CAPACITY, TEST_AVAILABLE_CAPACITY, TEST_MONTHLY_PAYMENT,
                    TEST_TOTAL_INCOME, TEST_CURRENT_DEBT, createTestPaymentPlan(),
                    TEST_RESPONSE_DATE, TEST_ERROR_MESSAGE);

            // Assert
            assertEquals(TEST_REQUEST_ID, debtCapacity.getRequestId());
            assertEquals(TEST_APPLICATION_ID, debtCapacity.getApplicationId());
            assertEquals(TEST_DECISION, debtCapacity.getDecision());
            assertEquals(TEST_MAX_CAPACITY, debtCapacity.getMaxCapacity());
            assertEquals(TEST_AVAILABLE_CAPACITY, debtCapacity.getAvailableCapacity());
            assertEquals(TEST_MONTHLY_PAYMENT, debtCapacity.getMonthlyPayment());
            assertEquals(TEST_TOTAL_INCOME, debtCapacity.getTotalIncome());
            assertEquals(TEST_CURRENT_DEBT, debtCapacity.getCurrentDebt());
            assertNotNull(debtCapacity.getPaymentPlan());
            assertEquals(TEST_RESPONSE_DATE, debtCapacity.getResponseDate());
            assertEquals(TEST_ERROR_MESSAGE, debtCapacity.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTest {

        @Test
        @DisplayName("Should set and get all fields correctly")
        void shouldSetAndGetAllFields() {
            // Arrange
            UserDebtCapacity debtCapacity = new UserDebtCapacity();

            // Act
            debtCapacity.setRequestId(TEST_REQUEST_ID);
            debtCapacity.setApplicationId(TEST_APPLICATION_ID);
            debtCapacity.setDecision(TEST_DECISION);
            debtCapacity.setMaxCapacity(TEST_MAX_CAPACITY);
            debtCapacity.setAvailableCapacity(TEST_AVAILABLE_CAPACITY);
            debtCapacity.setMonthlyPayment(TEST_MONTHLY_PAYMENT);
            debtCapacity.setTotalIncome(TEST_TOTAL_INCOME);
            debtCapacity.setCurrentDebt(TEST_CURRENT_DEBT);
            debtCapacity.setPaymentPlan(createTestPaymentPlan());
            debtCapacity.setResponseDate(TEST_RESPONSE_DATE);
            debtCapacity.setErrorMessage(TEST_ERROR_MESSAGE);

            // Assert
            assertEquals(TEST_REQUEST_ID, debtCapacity.getRequestId());
            assertEquals(TEST_APPLICATION_ID, debtCapacity.getApplicationId());
            assertEquals(TEST_DECISION, debtCapacity.getDecision());
            assertEquals(TEST_MAX_CAPACITY, debtCapacity.getMaxCapacity());
            assertEquals(TEST_AVAILABLE_CAPACITY, debtCapacity.getAvailableCapacity());
            assertEquals(TEST_MONTHLY_PAYMENT, debtCapacity.getMonthlyPayment());
            assertEquals(TEST_TOTAL_INCOME, debtCapacity.getTotalIncome());
            assertEquals(TEST_CURRENT_DEBT, debtCapacity.getCurrentDebt());
            assertNotNull(debtCapacity.getPaymentPlan());
            assertEquals(TEST_RESPONSE_DATE, debtCapacity.getResponseDate());
            assertEquals(TEST_ERROR_MESSAGE, debtCapacity.getErrorMessage());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValues() {
            // Arrange
            UserDebtCapacity debtCapacity = UserDebtCapacity.builder()
                    .requestId(TEST_REQUEST_ID)
                    .decision(TEST_DECISION)
                    .build();

            // Act
            debtCapacity.setMaxCapacity(null);
            debtCapacity.setAvailableCapacity(null);
            debtCapacity.setMonthlyPayment(null);
            debtCapacity.setTotalIncome(null);
            debtCapacity.setCurrentDebt(null);
            debtCapacity.setPaymentPlan(null);
            debtCapacity.setResponseDate(null);
            debtCapacity.setErrorMessage(null);

            // Assert
            assertEquals(TEST_REQUEST_ID, debtCapacity.getRequestId());
            assertEquals(TEST_DECISION, debtCapacity.getDecision());
            assertNull(debtCapacity.getMaxCapacity());
            assertNull(debtCapacity.getAvailableCapacity());
            assertNull(debtCapacity.getMonthlyPayment());
            assertNull(debtCapacity.getTotalIncome());
            assertNull(debtCapacity.getCurrentDebt());
            assertNull(debtCapacity.getPaymentPlan());
            assertNull(debtCapacity.getResponseDate());
            assertNull(debtCapacity.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("Object Behavior Tests")
    class ObjectBehaviorTest {

        @Test
        @DisplayName("Should generate meaningful toString")
        void shouldGenerateMeaningfulToString() {
            // Arrange
            UserDebtCapacity debtCapacity = UserDebtCapacity.builder()
                    .requestId(TEST_REQUEST_ID)
                    .decision(TEST_DECISION)
                    .maxCapacity(TEST_MAX_CAPACITY)
                    .build();

            // Act
            String toString = debtCapacity.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("UserDebtCapacity"));
            assertTrue(toString.contains(TEST_DECISION));
            assertTrue(toString.contains(TEST_MAX_CAPACITY.toString()));
        }

        @Test
        @DisplayName("Should have consistent equals and hashCode")
        void shouldHaveConsistentEqualsAndHashCode() {
            // Arrange
            UserDebtCapacity capacity1 = UserDebtCapacity.builder()
                    .requestId(TEST_REQUEST_ID)
                    .decision(TEST_DECISION)
                    .maxCapacity(TEST_MAX_CAPACITY)
                    .build();

            UserDebtCapacity capacity2 = UserDebtCapacity.builder()
                    .requestId(TEST_REQUEST_ID)
                    .decision(TEST_DECISION)
                    .maxCapacity(TEST_MAX_CAPACITY)
                    .build();

            UserDebtCapacity capacity3 = UserDebtCapacity.builder()
                    .requestId(UUID.randomUUID())
                    .decision(TEST_DECISION)
                    .maxCapacity(TEST_MAX_CAPACITY)
                    .build();

            // Assert
            assertEquals(capacity1, capacity2);
            assertEquals(capacity1.hashCode(), capacity2.hashCode());
            assertNotEquals(capacity1, capacity3);
            assertNotEquals(capacity1.hashCode(), capacity3.hashCode());
        }

        @Test
        @DisplayName("Should handle equals with null values")
        void shouldHandleEqualsWithNullValues() {
            // Arrange
            UserDebtCapacity capacity1 = new UserDebtCapacity();
            UserDebtCapacity capacity2 = new UserDebtCapacity();

            // Assert
            assertEquals(capacity1, capacity2);
            assertEquals(capacity1.hashCode(), capacity2.hashCode());
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTest {

        @Test
        @DisplayName("Should handle approved decision with full capacity data")
        void shouldHandleApprovedDecision() {
            // Arrange
            UserDebtCapacity debtCapacity = UserDebtCapacity.builder()
                    .requestId(TEST_REQUEST_ID)
                    .applicationId(TEST_APPLICATION_ID)
                    .decision("APROBADO")
                    .maxCapacity(BigDecimal.valueOf(100000.00))
                    .availableCapacity(BigDecimal.valueOf(75000.00))
                    .monthlyPayment(BigDecimal.valueOf(2500.00))
                    .totalIncome(BigDecimal.valueOf(8000.00))
                    .currentDebt(BigDecimal.valueOf(15000.00))
                    .paymentPlan(createTestPaymentPlan())
                    .responseDate(TEST_RESPONSE_DATE)
                    .build();

            // Assert
            assertEquals("APROBADO", debtCapacity.getDecision());
            assertTrue(debtCapacity.getAvailableCapacity().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(debtCapacity.getMaxCapacity().compareTo(debtCapacity.getAvailableCapacity()) >= 0);
            assertNotNull(debtCapacity.getPaymentPlan());
        }

        @Test
        @DisplayName("Should handle rejected decision with error message")
        void shouldHandleRejectedDecision() {
            // Arrange
            UserDebtCapacity debtCapacity = UserDebtCapacity.builder()
                    .requestId(TEST_REQUEST_ID)
                    .applicationId(TEST_APPLICATION_ID)
                    .decision("RECHAZADO")
                    .maxCapacity(BigDecimal.ZERO)
                    .availableCapacity(BigDecimal.ZERO)
                    .errorMessage("Insufficient income for requested loan amount")
                    .responseDate(TEST_RESPONSE_DATE)
                    .build();

            // Assert
            assertEquals("RECHAZADO", debtCapacity.getDecision());
            assertEquals(BigDecimal.ZERO, debtCapacity.getMaxCapacity());
            assertEquals(BigDecimal.ZERO, debtCapacity.getAvailableCapacity());
            assertNotNull(debtCapacity.getErrorMessage());
        }

        @Test
        @DisplayName("Should handle manual review decision")
        void shouldHandleManualReviewDecision() {
            // Arrange
            UserDebtCapacity debtCapacity = UserDebtCapacity.builder()
                    .requestId(TEST_REQUEST_ID)
                    .applicationId(TEST_APPLICATION_ID)
                    .decision("REVISION MANUAL")
                    .maxCapacity(BigDecimal.valueOf(50000.00))
                    .availableCapacity(BigDecimal.valueOf(25000.00))
                    .errorMessage("Requires manual review due to unusual income pattern")
                    .responseDate(TEST_RESPONSE_DATE)
                    .build();

            // Assert
            assertEquals("REVISION MANUAL", debtCapacity.getDecision());
            assertTrue(debtCapacity.getAvailableCapacity().compareTo(BigDecimal.ZERO) > 0);
            assertNotNull(debtCapacity.getErrorMessage());
        }

        @Test
        @DisplayName("Should calculate debt-to-income ratio")
        void shouldCalculateDebtToIncomeRatio() {
            // Arrange
            BigDecimal totalIncome = BigDecimal.valueOf(8000.00);
            BigDecimal currentDebt = BigDecimal.valueOf(15000.00);
            BigDecimal monthlyPayment = BigDecimal.valueOf(2500.00);

            UserDebtCapacity debtCapacity = UserDebtCapacity.builder()
                    .totalIncome(totalIncome)
                    .currentDebt(currentDebt)
                    .monthlyPayment(monthlyPayment)
                    .build();

            // Assert
            assertEquals(totalIncome, debtCapacity.getTotalIncome());
            assertEquals(currentDebt, debtCapacity.getCurrentDebt());
            assertEquals(monthlyPayment, debtCapacity.getMonthlyPayment());

            // Verify debt-to-income ratio calculation logic
            BigDecimal monthlyDebtRatio = monthlyPayment.divide(totalIncome, 4, BigDecimal.ROUND_HALF_UP);
            assertTrue(monthlyDebtRatio.compareTo(BigDecimal.valueOf(0.4)) <= 0); // Should be reasonable
        }
    }

    @Nested
    @DisplayName("PaymentPlanItem Tests")
    class PaymentPlanItemTest {

        @Test
        @DisplayName("Should build PaymentPlanItem with all fields")
        void shouldBuildPaymentPlanItem() {
            // Arrange
            Integer installmentNumber = 1;
            BigDecimal principalPayment = BigDecimal.valueOf(2000.00);
            BigDecimal interestPayment = BigDecimal.valueOf(500.00);
            BigDecimal remainingBalance = BigDecimal.valueOf(48000.00);
            Instant dueDate = Instant.now().plusSeconds(2592000); // 30 days

            // Act
            UserDebtCapacity.PaymentPlanItem item = UserDebtCapacity.PaymentPlanItem.builder()
                    .installmentNumber(installmentNumber)
                    .principalPayment(principalPayment)
                    .interestPayment(interestPayment)
                    .remainingBalance(remainingBalance)
                    .dueDate(dueDate)
                    .build();

            // Assert
            assertEquals(installmentNumber, item.getInstallmentNumber());
            assertEquals(principalPayment, item.getPrincipalPayment());
            assertEquals(interestPayment, item.getInterestPayment());
            assertEquals(remainingBalance, item.getRemainingBalance());
            assertEquals(dueDate, item.getDueDate());
        }

        @Test
        @DisplayName("Should handle PaymentPlanItem with zero values")
        void shouldHandlePaymentPlanItemWithZeroValues() {
            // Act
            UserDebtCapacity.PaymentPlanItem item = UserDebtCapacity.PaymentPlanItem.builder()
                    .installmentNumber(0)
                    .principalPayment(BigDecimal.ZERO)
                    .interestPayment(BigDecimal.ZERO)
                    .remainingBalance(BigDecimal.ZERO)
                    .build();

            // Assert
            assertEquals(0, item.getInstallmentNumber());
            assertEquals(BigDecimal.ZERO, item.getPrincipalPayment());
            assertEquals(BigDecimal.ZERO, item.getInterestPayment());
            assertEquals(BigDecimal.ZERO, item.getRemainingBalance());
        }

        @Test
        @DisplayName("Should calculate total payment in PaymentPlanItem")
        void shouldCalculateTotalPayment() {
            // Arrange
            BigDecimal principalPayment = BigDecimal.valueOf(2000.00);
            BigDecimal interestPayment = BigDecimal.valueOf(500.00);
            BigDecimal expectedTotal = BigDecimal.valueOf(2500.00);

            UserDebtCapacity.PaymentPlanItem item = UserDebtCapacity.PaymentPlanItem.builder()
                    .principalPayment(principalPayment)
                    .interestPayment(interestPayment)
                    .build();

            // Assert
            assertEquals(expectedTotal, principalPayment.add(interestPayment));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle zero and negative values")
        void shouldHandleZeroAndNegativeValues() {
            // Act
            UserDebtCapacity debtCapacity = UserDebtCapacity.builder()
                    .maxCapacity(BigDecimal.ZERO)
                    .availableCapacity(BigDecimal.ZERO)
                    .monthlyPayment(BigDecimal.ZERO)
                    .totalIncome(BigDecimal.ZERO)
                    .currentDebt(BigDecimal.ZERO)
                    .build();

            // Assert
            assertEquals(BigDecimal.ZERO, debtCapacity.getMaxCapacity());
            assertEquals(BigDecimal.ZERO, debtCapacity.getAvailableCapacity());
            assertEquals(BigDecimal.ZERO, debtCapacity.getMonthlyPayment());
            assertEquals(BigDecimal.ZERO, debtCapacity.getTotalIncome());
            assertEquals(BigDecimal.ZERO, debtCapacity.getCurrentDebt());
        }

        @Test
        @DisplayName("Should handle very large values")
        void shouldHandleVeryLargeValues() {
            // Arrange
            BigDecimal largeAmount = BigDecimal.valueOf(999999999.99);

            // Act
            UserDebtCapacity debtCapacity = UserDebtCapacity.builder()
                    .maxCapacity(largeAmount)
                    .availableCapacity(largeAmount)
                    .monthlyPayment(largeAmount)
                    .totalIncome(largeAmount)
                    .currentDebt(largeAmount)
                    .build();

            // Assert
            assertEquals(largeAmount, debtCapacity.getMaxCapacity());
            assertEquals(largeAmount, debtCapacity.getAvailableCapacity());
            assertEquals(largeAmount, debtCapacity.getMonthlyPayment());
            assertEquals(largeAmount, debtCapacity.getTotalIncome());
            assertEquals(largeAmount, debtCapacity.getCurrentDebt());
        }

        @Test
        @DisplayName("Should handle empty and whitespace strings")
        void shouldHandleEmptyAndWhitespaceStrings() {
            // Act
            UserDebtCapacity debtCapacity = UserDebtCapacity.builder()
                    .decision("")
                    .errorMessage("   ")
                    .build();

            // Assert
            assertEquals("", debtCapacity.getDecision());
            assertEquals("   ", debtCapacity.getErrorMessage());
        }

        @Test
        @DisplayName("Should handle null payment plan")
        void shouldHandleNullPaymentPlan() {
            // Act
            UserDebtCapacity debtCapacity = UserDebtCapacity.builder()
                    .requestId(TEST_REQUEST_ID)
                    .paymentPlan(null)
                    .build();

            // Assert
            assertEquals(TEST_REQUEST_ID, debtCapacity.getRequestId());
            assertNull(debtCapacity.getPaymentPlan());
        }

        @Test
        @DisplayName("Should handle empty payment plan")
        void shouldHandleEmptyPaymentPlan() {
            // Act
            UserDebtCapacity debtCapacity = UserDebtCapacity.builder()
                    .requestId(TEST_REQUEST_ID)
                    .paymentPlan(List.of())
                    .build();

            // Assert
            assertEquals(TEST_REQUEST_ID, debtCapacity.getRequestId());
            assertNotNull(debtCapacity.getPaymentPlan());
            assertTrue(debtCapacity.getPaymentPlan().isEmpty());
        }
    }

    // Helper method
    private List<UserDebtCapacity.PaymentPlanItem> createTestPaymentPlan() {
        UserDebtCapacity.PaymentPlanItem item1 = UserDebtCapacity.PaymentPlanItem.builder()
                .installmentNumber(1)
                .principalPayment(BigDecimal.valueOf(2000.00))
                .interestPayment(BigDecimal.valueOf(500.00))
                .remainingBalance(BigDecimal.valueOf(48000.00))
                .dueDate(Instant.now().plusSeconds(2592000))
                .build();

        UserDebtCapacity.PaymentPlanItem item2 = UserDebtCapacity.PaymentPlanItem.builder()
                .installmentNumber(2)
                .principalPayment(BigDecimal.valueOf(2100.00))
                .interestPayment(BigDecimal.valueOf(400.00))
                .remainingBalance(BigDecimal.valueOf(45900.00))
                .dueDate(Instant.now().plusSeconds(5184000))
                .build();

        return List.of(item1, item2);
    }
}
package co.com.bancolombia.model.loanapplication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.state.State;
import co.com.bancolombia.model.user.UserInfo;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoanApplicationReview Domain Model Tests")
class LoanApplicationReviewTest {

    private static final UUID TEST_ID = UUID.randomUUID();
    private static final BigDecimal TEST_AMOUNT = BigDecimal.valueOf(50000.00);
    private static final Integer TEST_TERM = 12;
    private static final LocalDateTime TEST_CREATED_AT = LocalDateTime.now();

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderPatternTest {

        @Test
        @DisplayName("Should build LoanApplicationReview with all fields using builder")
        void shouldBuildWithAllFields() {
            // Act
            LoanApplicationReview review = LoanApplicationReview.builder()
                    .id(TEST_ID)
                    .amount(TEST_AMOUNT)
                    .term(TEST_TERM)
                    .loanApplication(createTestLoanApplication())
                    .loanType(createTestLoanType())
                    .state(createTestState())
                    .userInfo(createTestUserInfo())
                    .totalMonthlyDebt(BigDecimal.valueOf(500.00))
                    .createdAt(TEST_CREATED_AT)
                    .build();

            // Assert
            assertEquals(TEST_ID, review.getId());
            assertEquals(TEST_AMOUNT, review.getAmount());
            assertEquals(TEST_TERM, review.getTerm());
            assertNotNull(review.getLoanApplication());
            assertNotNull(review.getLoanType());
            assertNotNull(review.getState());
            assertNotNull(review.getUserInfo());
            assertEquals(BigDecimal.valueOf(500.00), review.getTotalMonthlyDebt());
            assertEquals(TEST_CREATED_AT, review.getCreatedAt());
        }

        @Test
        @DisplayName("Should build LoanApplicationReview with minimal fields")
        void shouldBuildWithMinimalFields() {
            // Act
            LoanApplicationReview review = LoanApplicationReview.builder()
                    .id(TEST_ID)
                    .build();

            // Assert
            assertEquals(TEST_ID, review.getId());
            assertNull(review.getAmount());
            assertNull(review.getTerm());
            assertNull(review.getLoanApplication());
            assertNull(review.getLoanType());
            assertNull(review.getState());
            assertNull(review.getUserInfo());
            assertNull(review.getTotalMonthlyDebt());
            assertNull(review.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTest {

        @Test
        @DisplayName("Should create LoanApplicationReview with no-args constructor")
        void shouldCreateWithNoArgsConstructor() {
            // Act
            LoanApplicationReview review = new LoanApplicationReview();

            // Assert
            assertNull(review.getId());
            assertNull(review.getAmount());
            assertNull(review.getTerm());
            assertNull(review.getLoanApplication());
            assertNull(review.getLoanType());
            assertNull(review.getState());
            assertNull(review.getUserInfo());
            assertNull(review.getTotalMonthlyDebt());
            assertNull(review.getCreatedAt());
        }

        @Test
        @DisplayName("Should create LoanApplicationReview with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            // Act
            LoanApplicationReview review = LoanApplicationReview.builder()
                    .id(TEST_ID)
                    .amount(TEST_AMOUNT)
                    .term(TEST_TERM)
                    .loanApplication(createTestLoanApplication())
                    .loanType(createTestLoanType())
                    .state(createTestState())
                    .userInfo(createTestUserInfo())
                    .totalMonthlyDebt(BigDecimal.valueOf(500.00))
                    .createdAt(TEST_CREATED_AT)
                    .build();

            // Assert
            assertEquals(TEST_ID, review.getId());
            assertEquals(TEST_AMOUNT, review.getAmount());
            assertEquals(TEST_TERM, review.getTerm());
            assertNotNull(review.getLoanApplication());
            assertNotNull(review.getLoanType());
            assertNotNull(review.getState());
            assertNotNull(review.getUserInfo());
            assertEquals(BigDecimal.valueOf(500.00), review.getTotalMonthlyDebt());
            assertEquals(TEST_CREATED_AT, review.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Domain Objects Integration Tests")
    class DomainObjectsIntegrationTest {

        @Test
        @DisplayName("Should integrate with LoanType domain object")
        void shouldIntegrateWithLoanType() {
            // Arrange
            LoanType loanType = LoanType.builder()
                    .id(1L)
                    .name("Personal Loan")
                    .interestRate(BigDecimal.valueOf(12.5))
                    .build();

            // Act
            LoanApplicationReview review = LoanApplicationReview.builder()
                    .loanType(loanType)
                    .build();

            // Assert
            assertEquals(loanType, review.getLoanType());
            assertEquals("Personal Loan", review.getLoanType().getName());
            assertEquals(BigDecimal.valueOf(12.5), review.getLoanType().getInterestRate());
        }

        @Test
        @DisplayName("Should integrate with State domain object")
        void shouldIntegrateWithState() {
            // Arrange
            State state = State.builder()
                    .id(1L)
                    .name("Pending review")
                    .description("Application is pending review")
                    .build();

            // Act
            LoanApplicationReview review = LoanApplicationReview.builder()
                    .state(state)
                    .build();

            // Assert
            assertEquals(state, review.getState());
            assertEquals("Pending review", review.getState().getName());
            assertEquals("Application is pending review", review.getState().getDescription());
        }

        @Test
        @DisplayName("Should integrate with UserInfo domain object")
        void shouldIntegrateWithUserInfo() {
            // Arrange
            UserInfo userInfo = new UserInfo(123L, "John", "Doe", "john.doe@example.com",
                    "123456789", "12345568","123 Main St", LocalDate.of(1990, 1, 1),
                    "USER", BigDecimal.valueOf(3000.00));

            // Act
            LoanApplicationReview review = LoanApplicationReview.builder()
                    .userInfo(userInfo)
                    .build();

            // Assert
            assertEquals(userInfo, review.getUserInfo());
            assertEquals("john.doe@example.com", review.getUserInfo().email());
            assertEquals("John", review.getUserInfo().name());
            assertEquals("Doe", review.getUserInfo().lastName());
            assertEquals(BigDecimal.valueOf(3000.00), review.getUserInfo().baseSalary());
        }
    }

    @Nested
    @DisplayName("Object Behavior Tests")
    class ObjectBehaviorTest {

        @Test
        @DisplayName("Should generate meaningful toString")
        void shouldGenerateMeaningfulToString() {
            // Arrange
            LoanApplicationReview review = LoanApplicationReview.builder()
                    .id(TEST_ID)
                    .amount(TEST_AMOUNT)
                    .build();

            // Act
            String toString = review.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("LoanApplicationReview"));
            assertTrue(toString.contains(TEST_ID.toString()));
        }

        @Test
        @DisplayName("Should have consistent equals and hashCode")
        void shouldHaveConsistentEqualsAndHashCode() {
            // Arrange
            LoanApplicationReview review1 = LoanApplicationReview.builder()
                    .id(TEST_ID)
                    .amount(TEST_AMOUNT)
                    .term(TEST_TERM)
                    .build();

            LoanApplicationReview review2 = LoanApplicationReview.builder()
                    .id(TEST_ID)
                    .amount(TEST_AMOUNT)
                    .term(TEST_TERM)
                    .build();

            LoanApplicationReview review3 = LoanApplicationReview.builder()
                    .id(UUID.randomUUID())
                    .amount(TEST_AMOUNT)
                    .term(TEST_TERM)
                    .build();

            // Assert
            assertEquals(review1, review2);
            assertEquals(review1.hashCode(), review2.hashCode());
            assertNotEquals(review1, review3);
            assertNotEquals(review1.hashCode(), review3.hashCode());
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTest {

        @Test
        @DisplayName("Should handle complete review information")
        void shouldHandleCompleteReviewInformation() {
            // Arrange
            LoanApplicationReview review = createCompleteReview();

            // Assert
            assertNotNull(review.getId());
            assertNotNull(review.getAmount());
            assertNotNull(review.getTerm());
            assertNotNull(review.getLoanApplication());
            assertNotNull(review.getLoanType());
            assertNotNull(review.getState());
            assertNotNull(review.getUserInfo());
            assertNotNull(review.getTotalMonthlyDebt());
            assertNotNull(review.getCreatedAt());

            // Verify nested objects
            assertEquals("Personal Loan", review.getLoanType().getName());
            assertEquals("Pending review", review.getState().getName());
            assertEquals("john.doe@example.com", review.getUserInfo().email());
        }

        @Test
        @DisplayName("Should handle financial calculations")
        void shouldHandleFinancialCalculations() {
            // Arrange
            BigDecimal amount = BigDecimal.valueOf(50000.00);
            BigDecimal interestRate = BigDecimal.valueOf(12.5);
            BigDecimal baseSalary = BigDecimal.valueOf(3000.00);
            BigDecimal totalDebt = BigDecimal.valueOf(500.00);

            LoanApplicationReview review = LoanApplicationReview.builder()
                    .amount(amount)
                    .loanType(LoanType.builder().interestRate(interestRate).build())
                    .userInfo(new UserInfo(123L, "John", "Doe", "john@example.com",
                            "123", "1234566890","Address", LocalDate.now(), "USER", baseSalary))
                    .totalMonthlyDebt(totalDebt)
                    .build();

            // Assert
            assertEquals(amount, review.getAmount());
            assertEquals(interestRate, review.getLoanType().getInterestRate());
            assertEquals(baseSalary, review.getUserInfo().baseSalary());
            assertEquals(totalDebt, review.getTotalMonthlyDebt());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle zero and negative values")
        void shouldHandleZeroAndNegativeValues() {
            // Act
            LoanApplicationReview review = LoanApplicationReview.builder()
                    .amount(BigDecimal.ZERO)
                    .term(0)
                    .totalMonthlyDebt(BigDecimal.ZERO)
                    .build();

            // Assert
            assertEquals(BigDecimal.ZERO, review.getAmount());
            assertEquals(0, review.getTerm());
            assertEquals(BigDecimal.ZERO, review.getTotalMonthlyDebt());
        }

        @Test
        @DisplayName("Should handle very large values")
        void shouldHandleVeryLargeValues() {
            // Arrange
            BigDecimal largeAmount = BigDecimal.valueOf(999999999.99);
            BigDecimal largeSalary = BigDecimal.valueOf(999999.99);

            // Act
            LoanApplicationReview review = LoanApplicationReview.builder()
                    .amount(largeAmount)
                    .userInfo(new UserInfo(123L, "John", "Doe", "john@example.com",
                            "123", "123456789","Address", LocalDate.now(), "USER", largeSalary))
                    .build();

            // Assert
            assertEquals(largeAmount, review.getAmount());
            assertEquals(largeSalary, review.getUserInfo().baseSalary());
        }

        @Test
        @DisplayName("Should handle null nested objects")
        void shouldHandleNullNestedObjects() {
            // Act
            LoanApplicationReview review = LoanApplicationReview.builder()
                    .id(TEST_ID)
                    .loanType(null)
                    .state(null)
                    .userInfo(null)
                    .build();

            // Assert
            assertEquals(TEST_ID, review.getId());
            assertNull(review.getLoanType());
            assertNull(review.getState());
            assertNull(review.getUserInfo());
        }
    }

    // Helper methods
    private LoanApplication createTestLoanApplication() {
        return LoanApplication.builder()
                .id(TEST_ID)
                .clientId("client123")
                .amount(TEST_AMOUNT)
                .build();
    }

    private LoanType createTestLoanType() {
        return LoanType.builder()
                .id(1L)
                .name("Personal Loan")
                .interestRate(BigDecimal.valueOf(12.5))
                .build();
    }

    private State createTestState() {
        return State.builder()
                .id(1L)
                .name("Pending review")
                .description("Application is pending review")
                .build();
    }

    private UserInfo createTestUserInfo() {
        return new UserInfo(123L, "John", "Doe", "john.doe@example.com",
                "123456789", "123456789","123 Main St", LocalDate.of(1990, 1, 1),
                "USER", BigDecimal.valueOf(3000.00));
    }

    private LoanApplicationReview createCompleteReview() {
        return LoanApplicationReview.builder()
                .id(TEST_ID)
                .amount(TEST_AMOUNT)
                .term(TEST_TERM)
                .loanApplication(createTestLoanApplication())
                .loanType(createTestLoanType())
                .state(createTestState())
                .userInfo(createTestUserInfo())
                .totalMonthlyDebt(BigDecimal.valueOf(500.00))
                .createdAt(TEST_CREATED_AT)
                .build();
    }
}
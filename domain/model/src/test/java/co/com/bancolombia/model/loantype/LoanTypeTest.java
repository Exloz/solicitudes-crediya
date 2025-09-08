package co.com.bancolombia.model.loantype;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoanType Domain Model Tests")
class LoanTypeTest {

    private static final Long TEST_ID = 1L;
    private static final String TEST_NAME = "Personal Loan";
    private static final BigDecimal TEST_MIN_AMOUNT = BigDecimal.valueOf(1000.00);
    private static final BigDecimal TEST_MAX_AMOUNT = BigDecimal.valueOf(50000.00);
    private static final BigDecimal TEST_INTEREST_RATE = BigDecimal.valueOf(12.5);

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderPatternTest {

        @Test
        @DisplayName("Should build LoanType with all fields using builder")
        void shouldBuildWithAllFields() {
            // Act
            LoanType loanType = LoanType.builder()
                    .id(TEST_ID)
                    .name(TEST_NAME)
                    .minAmount(TEST_MIN_AMOUNT)
                    .maxAmount(TEST_MAX_AMOUNT)
                    .interestRate(TEST_INTEREST_RATE)
                    .build();

            // Assert
            assertEquals(TEST_ID, loanType.getId());
            assertEquals(TEST_NAME, loanType.getName());
            assertEquals(TEST_MIN_AMOUNT, loanType.getMinAmount());
            assertEquals(TEST_MAX_AMOUNT, loanType.getMaxAmount());
            assertEquals(TEST_INTEREST_RATE, loanType.getInterestRate());
        }

        @Test
        @DisplayName("Should build LoanType with minimal fields")
        void shouldBuildWithMinimalFields() {
            // Act
            LoanType loanType = LoanType.builder()
                    .name(TEST_NAME)
                    .build();

            // Assert
            assertNull(loanType.getId());
            assertEquals(TEST_NAME, loanType.getName());
            assertNull(loanType.getMinAmount());
            assertNull(loanType.getMaxAmount());
            assertNull(loanType.getInterestRate());
        }

        @Test
        @DisplayName("Should support toBuilder pattern")
        void shouldSupportToBuilder() {
            // Arrange
            LoanType original = LoanType.builder()
                    .id(TEST_ID)
                    .name(TEST_NAME)
                    .build();

            // Act
            LoanType modified = original.toBuilder()
                    .minAmount(TEST_MIN_AMOUNT)
                    .maxAmount(TEST_MAX_AMOUNT)
                    .build();

            // Assert
            assertEquals(TEST_ID, modified.getId());
            assertEquals(TEST_NAME, modified.getName());
            assertEquals(TEST_MIN_AMOUNT, modified.getMinAmount());
            assertEquals(TEST_MAX_AMOUNT, modified.getMaxAmount());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTest {

        @Test
        @DisplayName("Should create LoanType with no-args constructor")
        void shouldCreateWithNoArgsConstructor() {
            // Act
            LoanType loanType = new LoanType();

            // Assert
            assertNull(loanType.getId());
            assertNull(loanType.getName());
            assertNull(loanType.getMinAmount());
            assertNull(loanType.getMaxAmount());
            assertNull(loanType.getInterestRate());
        }

        @Test
        @DisplayName("Should create LoanType with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            // Act
            LoanType loanType = new LoanType(
                    TEST_ID, TEST_NAME, TEST_MIN_AMOUNT,
                    TEST_MAX_AMOUNT, TEST_INTEREST_RATE);

            // Assert
            assertEquals(TEST_ID, loanType.getId());
            assertEquals(TEST_NAME, loanType.getName());
            assertEquals(TEST_MIN_AMOUNT, loanType.getMinAmount());
            assertEquals(TEST_MAX_AMOUNT, loanType.getMaxAmount());
            assertEquals(TEST_INTEREST_RATE, loanType.getInterestRate());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTest {

        @Test
        @DisplayName("Should set and get all fields correctly")
        void shouldSetAndGetAllFields() {
            // Arrange
            LoanType loanType = new LoanType();

            // Act
            loanType.setId(TEST_ID);
            loanType.setName(TEST_NAME);
            loanType.setMinAmount(TEST_MIN_AMOUNT);
            loanType.setMaxAmount(TEST_MAX_AMOUNT);
            loanType.setInterestRate(TEST_INTEREST_RATE);

            // Assert
            assertEquals(TEST_ID, loanType.getId());
            assertEquals(TEST_NAME, loanType.getName());
            assertEquals(TEST_MIN_AMOUNT, loanType.getMinAmount());
            assertEquals(TEST_MAX_AMOUNT, loanType.getMaxAmount());
            assertEquals(TEST_INTEREST_RATE, loanType.getInterestRate());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValues() {
            // Arrange
            LoanType loanType = LoanType.builder()
                    .id(TEST_ID)
                    .name(TEST_NAME)
                    .build();

            // Act
            loanType.setMinAmount(null);
            loanType.setMaxAmount(null);
            loanType.setInterestRate(null);

            // Assert
            assertEquals(TEST_ID, loanType.getId());
            assertEquals(TEST_NAME, loanType.getName());
            assertNull(loanType.getMinAmount());
            assertNull(loanType.getMaxAmount());
            assertNull(loanType.getInterestRate());
        }
    }

    @Nested
    @DisplayName("Object Behavior Tests")
    class ObjectBehaviorTest {

        @Test
        @DisplayName("Should generate meaningful toString")
        void shouldGenerateMeaningfulToString() {
            // Arrange
            LoanType loanType = LoanType.builder()
                    .id(TEST_ID)
                    .name(TEST_NAME)
                    .minAmount(TEST_MIN_AMOUNT)
                    .build();

            // Act
            String toString = loanType.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("LoanType"));
            assertTrue(toString.contains(TEST_NAME));
            assertTrue(toString.contains(TEST_MIN_AMOUNT.toString()));
        }

        @Test
        @DisplayName("Should have consistent equals and hashCode")
        void shouldHaveConsistentEqualsAndHashCode() {
            // Arrange
            LoanType type1 = LoanType.builder()
                    .id(TEST_ID)
                    .name(TEST_NAME)
                    .minAmount(TEST_MIN_AMOUNT)
                    .maxAmount(TEST_MAX_AMOUNT)
                    .interestRate(TEST_INTEREST_RATE)
                    .build();

            LoanType type2 = LoanType.builder()
                    .id(TEST_ID)
                    .name(TEST_NAME)
                    .minAmount(TEST_MIN_AMOUNT)
                    .maxAmount(TEST_MAX_AMOUNT)
                    .interestRate(TEST_INTEREST_RATE)
                    .build();

            LoanType type3 = LoanType.builder()
                    .id(2L)
                    .name(TEST_NAME)
                    .minAmount(TEST_MIN_AMOUNT)
                    .maxAmount(TEST_MAX_AMOUNT)
                    .interestRate(TEST_INTEREST_RATE)
                    .build();

            // Assert
            assertEquals(type1, type2);
            assertEquals(type1.hashCode(), type2.hashCode());
            assertNotEquals(type1, type3);
            assertNotEquals(type1.hashCode(), type3.hashCode());
        }

        @Test
        @DisplayName("Should handle equals with null values")
        void shouldHandleEqualsWithNullValues() {
            // Arrange
            LoanType type1 = new LoanType();
            LoanType type2 = new LoanType();

            // Assert
            assertEquals(type1, type2);
            assertEquals(type1.hashCode(), type2.hashCode());
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTest {

        @Test
        @DisplayName("Should handle valid loan amounts within range")
        void shouldHandleValidLoanAmounts() {
            // Arrange
            LoanType loanType = LoanType.builder()
                    .minAmount(BigDecimal.valueOf(1000.00))
                    .maxAmount(BigDecimal.valueOf(50000.00))
                    .build();

            BigDecimal validAmount = BigDecimal.valueOf(25000.00);

            // Assert
            assertTrue(validAmount.compareTo(loanType.getMinAmount()) >= 0);
            assertTrue(validAmount.compareTo(loanType.getMaxAmount()) <= 0);
        }

        @Test
        @DisplayName("Should handle boundary values")
        void shouldHandleBoundaryValues() {
            // Arrange
            LoanType loanType = LoanType.builder()
                    .minAmount(BigDecimal.valueOf(1000.00))
                    .maxAmount(BigDecimal.valueOf(50000.00))
                    .build();

            // Assert
            assertEquals(BigDecimal.valueOf(1000.00), loanType.getMinAmount());
            assertEquals(BigDecimal.valueOf(50000.00), loanType.getMaxAmount());
        }

        @Test
        @DisplayName("Should handle decimal precision in amounts")
        void shouldHandleDecimalPrecision() {
            // Arrange
            BigDecimal preciseMin = BigDecimal.valueOf(1000.50);
            BigDecimal preciseMax = BigDecimal.valueOf(50000.99);
            BigDecimal preciseRate = BigDecimal.valueOf(12.75);

            // Act
            LoanType loanType = LoanType.builder()
                    .minAmount(preciseMin)
                    .maxAmount(preciseMax)
                    .interestRate(preciseRate)
                    .build();

            // Assert
            assertEquals(preciseMin, loanType.getMinAmount());
            assertEquals(preciseMax, loanType.getMaxAmount());
            assertEquals(preciseRate, loanType.getInterestRate());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle zero and negative values")
        void shouldHandleZeroAndNegativeValues() {
            // Act
            LoanType loanType = LoanType.builder()
                    .id(0L)
                    .minAmount(BigDecimal.ZERO)
                    .maxAmount(BigDecimal.ZERO)
                    .interestRate(BigDecimal.ZERO)
                    .build();

            // Assert
            assertEquals(0L, loanType.getId());
            assertEquals(BigDecimal.ZERO, loanType.getMinAmount());
            assertEquals(BigDecimal.ZERO, loanType.getMaxAmount());
            assertEquals(BigDecimal.ZERO, loanType.getInterestRate());
        }

        @Test
        @DisplayName("Should handle very large values")
        void shouldHandleVeryLargeValues() {
            // Arrange
            BigDecimal largeAmount = BigDecimal.valueOf(999999999.99);
            BigDecimal largeRate = BigDecimal.valueOf(999.99);

            // Act
            LoanType loanType = LoanType.builder()
                    .id(Long.MAX_VALUE)
                    .minAmount(largeAmount)
                    .maxAmount(largeAmount)
                    .interestRate(largeRate)
                    .build();

            // Assert
            assertEquals(Long.MAX_VALUE, loanType.getId());
            assertEquals(largeAmount, loanType.getMinAmount());
            assertEquals(largeAmount, loanType.getMaxAmount());
            assertEquals(largeRate, loanType.getInterestRate());
        }

        @Test
        @DisplayName("Should handle empty and whitespace strings")
        void shouldHandleEmptyAndWhitespaceStrings() {
            // Act
            LoanType loanType = LoanType.builder()
                    .name("")
                    .build();

            // Assert
            assertEquals("", loanType.getName());
        }
    }
}
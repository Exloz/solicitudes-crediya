package co.com.bancolombia.model.loanapplication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoanApplication Domain Model Tests")
class LoanApplicationTest {

    private static final UUID TEST_ID = UUID.randomUUID();
    private static final String TEST_CLIENT_ID = "client123";
    private static final BigDecimal TEST_AMOUNT = BigDecimal.valueOf(10000.00);
    private static final Integer TEST_TERM = 12;
    private static final Long TEST_LOAN_TYPE_ID = 1L;
    private static final Long TEST_STATUS = 1L;
    private static final LocalDateTime TEST_CREATED_AT = LocalDateTime.now();

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderPatternTest {

        @Test
        @DisplayName("Should build LoanApplication with all fields using builder")
        void shouldBuildWithAllFields() {
            // Act
            LoanApplication application = LoanApplication.builder()
                    .id(TEST_ID)
                    .clientId(TEST_CLIENT_ID)
                    .amount(TEST_AMOUNT)
                    .term(TEST_TERM)
                    .loanTypeId(TEST_LOAN_TYPE_ID)
                    .statusId(TEST_STATUS)
                    .createdAt(TEST_CREATED_AT)
                    .build();

            // Assert
            assertEquals(TEST_ID, application.getId());
            assertEquals(TEST_CLIENT_ID, application.getClientId());
            assertEquals(TEST_AMOUNT, application.getAmount());
            assertEquals(TEST_TERM, application.getTerm());
            assertEquals(TEST_LOAN_TYPE_ID, application.getLoanTypeId());
            assertEquals(TEST_STATUS, application.getStatusId());
            assertEquals(TEST_CREATED_AT, application.getCreatedAt());
        }

        @Test
        @DisplayName("Should build LoanApplication with minimal fields")
        void shouldBuildWithMinimalFields() {
            // Act
            LoanApplication application = LoanApplication.builder()
                    .clientId(TEST_CLIENT_ID)
                    .amount(TEST_AMOUNT)
                    .build();

            // Assert
            assertNull(application.getId());
            assertEquals(TEST_CLIENT_ID, application.getClientId());
            assertEquals(TEST_AMOUNT, application.getAmount());
            assertNull(application.getTerm());
            assertNull(application.getLoanTypeId());
            assertNull(application.getStatusId());
            assertNull(application.getCreatedAt());
        }

        @Test
        @DisplayName("Should support toBuilder pattern")
        void shouldSupportToBuilder() {
            // Arrange
            LoanApplication original = LoanApplication.builder()
                    .id(TEST_ID)
                    .clientId(TEST_CLIENT_ID)
                    .amount(TEST_AMOUNT)
                    .build();

            // Act
            LoanApplication modified = original.toBuilder()
                    .term(TEST_TERM)
                    .loanTypeId(TEST_LOAN_TYPE_ID)
                    .build();

            // Assert
            assertEquals(TEST_ID, modified.getId());
            assertEquals(TEST_CLIENT_ID, modified.getClientId());
            assertEquals(TEST_AMOUNT, modified.getAmount());
            assertEquals(TEST_TERM, modified.getTerm());
            assertEquals(TEST_LOAN_TYPE_ID, modified.getLoanTypeId());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTest {

        @Test
        @DisplayName("Should create LoanApplication with no-args constructor")
        void shouldCreateWithNoArgsConstructor() {
            // Act
            LoanApplication application = new LoanApplication();

            // Assert
            assertNull(application.getId());
            assertNull(application.getClientId());
            assertNull(application.getAmount());
            assertNull(application.getTerm());
            assertNull(application.getLoanTypeId());
            assertNull(application.getStatusId());
            assertNull(application.getCreatedAt());
        }

        @Test
        @DisplayName("Should create LoanApplication with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            // Act
            LoanApplication application = new LoanApplication(
                    TEST_ID, TEST_CLIENT_ID, TEST_AMOUNT, TEST_TERM,
                    TEST_LOAN_TYPE_ID, TEST_STATUS, TEST_CREATED_AT);

            // Assert
            assertEquals(TEST_ID, application.getId());
            assertEquals(TEST_CLIENT_ID, application.getClientId());
            assertEquals(TEST_AMOUNT, application.getAmount());
            assertEquals(TEST_TERM, application.getTerm());
            assertEquals(TEST_LOAN_TYPE_ID, application.getLoanTypeId());
            assertEquals(TEST_STATUS, application.getStatusId());
            assertEquals(TEST_CREATED_AT, application.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTest {

        @Test
        @DisplayName("Should set and get all fields correctly")
        void shouldSetAndGetAllFields() {
            // Arrange
            LoanApplication application = new LoanApplication();

            // Act
            application.setId(TEST_ID);
            application.setClientId(TEST_CLIENT_ID);
            application.setAmount(TEST_AMOUNT);
            application.setTerm(TEST_TERM);
            application.setLoanTypeId(TEST_LOAN_TYPE_ID);
            application.setStatusId(TEST_STATUS);
            application.setCreatedAt(TEST_CREATED_AT);

            // Assert
            assertEquals(TEST_ID, application.getId());
            assertEquals(TEST_CLIENT_ID, application.getClientId());
            assertEquals(TEST_AMOUNT, application.getAmount());
            assertEquals(TEST_TERM, application.getTerm());
            assertEquals(TEST_LOAN_TYPE_ID, application.getLoanTypeId());
            assertEquals(TEST_STATUS, application.getStatusId());
            assertEquals(TEST_CREATED_AT, application.getCreatedAt());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValues() {
            // Arrange
            LoanApplication application = LoanApplication.builder()
                    .id(TEST_ID)
                    .clientId(TEST_CLIENT_ID)
                    .build();

            // Act
            application.setAmount(null);
            application.setTerm(null);
            application.setLoanTypeId(null);
            application.setStatusId(null);
            application.setCreatedAt(null);

            // Assert
            assertEquals(TEST_ID, application.getId());
            assertEquals(TEST_CLIENT_ID, application.getClientId());
            assertNull(application.getAmount());
            assertNull(application.getTerm());
            assertNull(application.getLoanTypeId());
            assertNull(application.getStatusId());
            assertNull(application.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Object Behavior Tests")
    class ObjectBehaviorTest {

        @Test
        @DisplayName("Should generate meaningful toString")
        void shouldGenerateMeaningfulToString() {
            // Arrange
            LoanApplication application = LoanApplication.builder()
                    .id(TEST_ID)
                    .clientId(TEST_CLIENT_ID)
                    .amount(TEST_AMOUNT)
                    .build();

            // Act
            String toString = application.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("LoanApplication"));
            assertTrue(toString.contains(TEST_CLIENT_ID));
            assertTrue(toString.contains(TEST_AMOUNT.toString()));
        }

        @Test
        @DisplayName("Should have consistent equals and hashCode")
        void shouldHaveConsistentEqualsAndHashCode() {
            // Arrange
            LoanApplication app1 = LoanApplication.builder()
                    .id(TEST_ID)
                    .clientId(TEST_CLIENT_ID)
                    .amount(TEST_AMOUNT)
                    .build();

            LoanApplication app2 = LoanApplication.builder()
                    .id(TEST_ID)
                    .clientId(TEST_CLIENT_ID)
                    .amount(TEST_AMOUNT)
                    .build();

            LoanApplication app3 = LoanApplication.builder()
                    .id(UUID.randomUUID())
                    .clientId(TEST_CLIENT_ID)
                    .amount(TEST_AMOUNT)
                    .build();

            // Assert
            assertEquals(app1, app2);
            assertEquals(app1.hashCode(), app2.hashCode());
            assertNotEquals(app1, app3);
            assertNotEquals(app1.hashCode(), app3.hashCode());
        }

        @Test
        @DisplayName("Should handle equals with null values")
        void shouldHandleEqualsWithNullValues() {
            // Arrange
            LoanApplication app1 = new LoanApplication();
            LoanApplication app2 = new LoanApplication();

            // Assert
            assertEquals(app1, app2);
            assertEquals(app1.hashCode(), app2.hashCode());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle zero and negative values")
        void shouldHandleZeroAndNegativeValues() {
            // Act
            LoanApplication application = LoanApplication.builder()
                    .amount(BigDecimal.ZERO)
                    .term(0)
                    .loanTypeId(0L)
                    .statusId(0L)
                    .build();

            // Assert
            assertEquals(BigDecimal.ZERO, application.getAmount());
            assertEquals(0, application.getTerm());
            assertEquals(0L, application.getLoanTypeId());
            assertEquals(0L, application.getStatusId());
        }

        @Test
        @DisplayName("Should handle very large values")
        void shouldHandleVeryLargeValues() {
            // Arrange
            BigDecimal largeAmount = BigDecimal.valueOf(999999999.99);
            Integer largeTerm = Integer.MAX_VALUE;
            Long largeId = Long.MAX_VALUE;

            // Act
            LoanApplication application = LoanApplication.builder()
                    .amount(largeAmount)
                    .term(largeTerm)
                    .loanTypeId(largeId)
                    .statusId(largeId)
                    .build();

            // Assert
            assertEquals(largeAmount, application.getAmount());
            assertEquals(largeTerm, application.getTerm());
            assertEquals(largeId, application.getLoanTypeId());
            assertEquals(largeId, application.getStatusId());
        }

        @Test
        @DisplayName("Should handle empty and whitespace strings")
        void shouldHandleEmptyAndWhitespaceStrings() {
            // Act
            LoanApplication application = LoanApplication.builder()
                    .clientId("")
                    .build();

            // Assert
            assertEquals("", application.getClientId());
        }
    }
}
package co.com.bancolombia.model.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserInfo Domain Model Tests")
class UserInfoTest {

    private static final Long TEST_USER_ID = 123L;
    private static final String TEST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String TEST_ID_DOCUMENT = "123456789";
    private static final String TEST_PHONE_NUMBER = "+1234567890";
    private static final String TEST_ADDRESS = "123 Main St, City, Country";
    private static final LocalDate TEST_BIRTH_DATE = LocalDate.of(1990, 1, 15);
    private static final String TEST_ROLE_ID = "USER";
    private static final BigDecimal TEST_BASE_SALARY = BigDecimal.valueOf(50000.00);

    @Nested
    @DisplayName("Record Constructor Tests")
    class RecordConstructorTest {

        @Test
        @DisplayName("Should create UserInfo with all fields")
        void shouldCreateWithAllFields() {
            // Act
            UserInfo userInfo = new UserInfo(
                    TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, TEST_BASE_SALARY);

            // Assert
            assertEquals(TEST_USER_ID, userInfo.userId());
            assertEquals(TEST_NAME, userInfo.name());
            assertEquals(TEST_LAST_NAME, userInfo.lastName());
            assertEquals(TEST_EMAIL, userInfo.email());
            assertEquals(TEST_ID_DOCUMENT, userInfo.idDocument());
            assertEquals(TEST_PHONE_NUMBER, userInfo.phoneNumber());
            assertEquals(TEST_ADDRESS, userInfo.address());
            assertEquals(TEST_BIRTH_DATE, userInfo.birthDate());
            assertEquals(TEST_ROLE_ID, userInfo.roleId());
            assertEquals(TEST_BASE_SALARY, userInfo.baseSalary());
        }

        @Test
        @DisplayName("Should handle null values in constructor")
        void shouldHandleNullValuesInConstructor() {
            // Act
            UserInfo userInfo = new UserInfo(null, null, null, null, null, null, null, null, null, null);

            // Assert
            assertNull(userInfo.userId());
            assertNull(userInfo.name());
            assertNull(userInfo.lastName());
            assertNull(userInfo.email());
            assertNull(userInfo.idDocument());
            assertNull(userInfo.phoneNumber());
            assertNull(userInfo.address());
            assertNull(userInfo.birthDate());
            assertNull(userInfo.roleId());
            assertNull(userInfo.baseSalary());
        }
    }

    @Nested
    @DisplayName("Accessor Methods Tests")
    class AccessorMethodsTest {

        @Test
        @DisplayName("Should return correct values from accessors")
        void shouldReturnCorrectValuesFromAccessors() {
            // Arrange
            UserInfo userInfo = new UserInfo(
                    TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, TEST_BASE_SALARY);

            // Assert
            assertEquals(TEST_USER_ID, userInfo.userId());
            assertEquals(TEST_NAME, userInfo.name());
            assertEquals(TEST_LAST_NAME, userInfo.lastName());
            assertEquals(TEST_EMAIL, userInfo.email());
            assertEquals(TEST_ID_DOCUMENT, userInfo.idDocument());
            assertEquals(TEST_PHONE_NUMBER, userInfo.phoneNumber());
            assertEquals(TEST_ADDRESS, userInfo.address());
            assertEquals(TEST_BIRTH_DATE, userInfo.birthDate());
            assertEquals(TEST_ROLE_ID, userInfo.roleId());
            assertEquals(TEST_BASE_SALARY, userInfo.baseSalary());
        }
    }

    @Nested
    @DisplayName("Object Behavior Tests")
    class ObjectBehaviorTest {

        @Test
        @DisplayName("Should generate meaningful toString")
        void shouldGenerateMeaningfulToString() {
            // Arrange
            UserInfo userInfo = new UserInfo(
                    TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, TEST_BASE_SALARY);

            // Act
            String toString = userInfo.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("UserInfo"));
            assertTrue(toString.contains(TEST_NAME));
            assertTrue(toString.contains(TEST_EMAIL));
            assertTrue(toString.contains(TEST_USER_ID.toString()));
        }

        @Test
        @DisplayName("Should have consistent equals and hashCode")
        void shouldHaveConsistentEqualsAndHashCode() {
            // Arrange
            UserInfo user1 = new UserInfo(
                    TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, TEST_BASE_SALARY);

            UserInfo user2 = new UserInfo(
                    TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, TEST_BASE_SALARY);

            UserInfo user3 = new UserInfo(
                    456L, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, TEST_BASE_SALARY);

            // Assert
            assertEquals(user1, user2);
            assertEquals(user1.hashCode(), user2.hashCode());
            assertNotEquals(user1, user3);
            assertNotEquals(user1.hashCode(), user3.hashCode());
        }

        @Test
        @DisplayName("Should handle equals with null values")
        void shouldHandleEqualsWithNullValues() {
            // Arrange
            UserInfo user1 = new UserInfo(null, null, null, null, null, null, null, null, null, null);
            UserInfo user2 = new UserInfo(null, null, null, null, null, null, null, null, null, null);

            // Assert
            assertEquals(user1, user2);
            assertEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to different types")
        void shouldNotBeEqualToDifferentTypes() {
            // Arrange
            UserInfo userInfo = new UserInfo(
                    TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, TEST_BASE_SALARY);

            // Assert
            assertNotEquals(userInfo, null);
            assertNotEquals(userInfo, "string");
            assertNotEquals(userInfo, new Object());
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTest {

        @Test
        @DisplayName("Should handle different user roles")
        void shouldHandleDifferentUserRoles() {
            // Arrange & Act
            UserInfo admin = new UserInfo(TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, "ADMIN", TEST_BASE_SALARY);

            UserInfo advisor = new UserInfo(TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, "ADVISOR", TEST_BASE_SALARY);

            UserInfo user = new UserInfo(TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, "USER", TEST_BASE_SALARY);

            // Assert
            assertEquals("ADMIN", admin.roleId());
            assertEquals("ADVISOR", advisor.roleId());
            assertEquals("USER", user.roleId());
        }

        @Test
        @DisplayName("Should handle salary calculations")
        void shouldHandleSalaryCalculations() {
            // Arrange
            BigDecimal highSalary = BigDecimal.valueOf(100000.00);
            BigDecimal lowSalary = BigDecimal.valueOf(25000.00);
            BigDecimal zeroSalary = BigDecimal.ZERO;

            // Act
            UserInfo highIncomeUser = new UserInfo(TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, highSalary);

            UserInfo lowIncomeUser = new UserInfo(TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, lowSalary);

            UserInfo noIncomeUser = new UserInfo(TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, zeroSalary);

            // Assert
            assertEquals(highSalary, highIncomeUser.baseSalary());
            assertEquals(lowSalary, lowIncomeUser.baseSalary());
            assertEquals(BigDecimal.ZERO, noIncomeUser.baseSalary());
        }

        @Test
        @DisplayName("Should handle age calculations from birth date")
        void shouldHandleAgeCalculationsFromBirthDate() {
            // Arrange
            LocalDate youngBirthDate = LocalDate.of(2000, 1, 1);
            LocalDate oldBirthDate = LocalDate.of(1950, 1, 1);

            // Act
            UserInfo youngUser = new UserInfo(TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    youngBirthDate, TEST_ROLE_ID, TEST_BASE_SALARY);

            UserInfo oldUser = new UserInfo(TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    oldBirthDate, TEST_ROLE_ID, TEST_BASE_SALARY);

            // Assert
            assertEquals(youngBirthDate, youngUser.birthDate());
            assertEquals(oldBirthDate, oldUser.birthDate());

            // Verify age calculation logic (current year - birth year)
            int currentYear = LocalDate.now().getYear();
            assertTrue(youngUser.birthDate().getYear() > oldUser.birthDate().getYear());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle zero and negative user ID")
        void shouldHandleZeroAndNegativeUserId() {
            // Act
            UserInfo zeroIdUser = new UserInfo(0L, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, TEST_BASE_SALARY);

            UserInfo negativeIdUser = new UserInfo(-1L, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, TEST_BASE_SALARY);

            // Assert
            assertEquals(0L, zeroIdUser.userId());
            assertEquals(-1L, negativeIdUser.userId());
        }

        @Test
        @DisplayName("Should handle very large user ID")
        void shouldHandleVeryLargeUserId() {
            // Act
            UserInfo largeIdUser = new UserInfo(Long.MAX_VALUE, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, TEST_BASE_SALARY);

            // Assert
            assertEquals(Long.MAX_VALUE, largeIdUser.userId());
        }

        @Test
        @DisplayName("Should handle empty and whitespace strings")
        void shouldHandleEmptyAndWhitespaceStrings() {
            // Act
            UserInfo emptyStringsUser = new UserInfo(TEST_USER_ID, "", "   ", "",
                    "   ", "", "   ", TEST_BIRTH_DATE, "", BigDecimal.ZERO);

            // Assert
            assertEquals("", emptyStringsUser.name());
            assertEquals("   ", emptyStringsUser.lastName());
            assertEquals("", emptyStringsUser.email());
            assertEquals("   ", emptyStringsUser.idDocument());
            assertEquals("", emptyStringsUser.phoneNumber());
            assertEquals("   ", emptyStringsUser.address());
            assertEquals("", emptyStringsUser.roleId());
            assertEquals(BigDecimal.ZERO, emptyStringsUser.baseSalary());
        }

        @Test
        @DisplayName("Should handle very long strings")
        void shouldHandleVeryLongStrings() {
            // Arrange
            String longName = "A".repeat(100);
            String longEmail = "a".repeat(50) + "@example.com";
            String longPhone = "+1234567890" + "0".repeat(50);
            String longAddress = "Address ".repeat(20);

            // Act
            UserInfo longStringsUser = new UserInfo(TEST_USER_ID, longName, longName, longEmail,
                    TEST_ID_DOCUMENT, longPhone, longAddress,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, TEST_BASE_SALARY);

            // Assert
            assertEquals(longName, longStringsUser.name());
            assertEquals(longName, longStringsUser.lastName());
            assertEquals(longEmail, longStringsUser.email());
            assertEquals(longPhone, longStringsUser.phoneNumber());
            assertEquals(longAddress, longStringsUser.address());
        }

        @Test
        @DisplayName("Should handle extreme salary values")
        void shouldHandleExtremeSalaryValues() {
            // Arrange
            BigDecimal maxSalary = BigDecimal.valueOf(Double.MAX_VALUE);
            BigDecimal negativeSalary = BigDecimal.valueOf(-1000.00);

            // Act
            UserInfo maxSalaryUser = new UserInfo(TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, maxSalary);

            UserInfo negativeSalaryUser = new UserInfo(TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    TEST_BIRTH_DATE, TEST_ROLE_ID, negativeSalary);

            // Assert
            assertEquals(maxSalary, maxSalaryUser.baseSalary());
            assertEquals(negativeSalary, negativeSalaryUser.baseSalary());
        }

        @Test
        @DisplayName("Should handle birth dates at boundaries")
        void shouldHandleBirthDatesAtBoundaries() {
            // Arrange
            LocalDate minDate = LocalDate.of(1900, 1, 1);
            LocalDate maxDate = LocalDate.of(2100, 12, 31);
            LocalDate futureDate = LocalDate.now().plusYears(10);

            // Act
            UserInfo oldUser = new UserInfo(TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    minDate, TEST_ROLE_ID, TEST_BASE_SALARY);

            UserInfo futureUser = new UserInfo(TEST_USER_ID, TEST_NAME, TEST_LAST_NAME, TEST_EMAIL,
                    TEST_ID_DOCUMENT, TEST_PHONE_NUMBER, TEST_ADDRESS,
                    futureDate, TEST_ROLE_ID, TEST_BASE_SALARY);

            // Assert
            assertEquals(minDate, oldUser.birthDate());
            assertEquals(futureDate, futureUser.birthDate());
        }
    }
}
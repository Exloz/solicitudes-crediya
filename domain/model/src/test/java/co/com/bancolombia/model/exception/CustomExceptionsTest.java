package co.com.bancolombia.model.exception;

import co.com.bancolombia.model.exception.business.InvalidLoanAmountException;
import co.com.bancolombia.model.exception.business.LoanApplicationNotFoundException;
import co.com.bancolombia.model.exception.business.LoanTypeNotFoundException;
import co.com.bancolombia.model.exception.business.StateNotFoundException;
import co.com.bancolombia.model.exception.security.ExpiredJwtTokenException;
import co.com.bancolombia.model.exception.security.InsufficientPrivilegesException;
import co.com.bancolombia.model.exception.security.InvalidJwtTokenException;
import co.com.bancolombia.model.exception.security.MissingAuthorizationHeaderException;
import co.com.bancolombia.model.exception.security.UserIdMismatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain Model Exceptions Tests")
class CustomExceptionsTest {

    @Nested
    @DisplayName("Business Exceptions")
    class BusinessExceptionsTest {

        @Test
        @DisplayName("InvalidLoanAmountException should store message correctly")
        void invalidLoanAmountException_withMessage() {
            // Arrange
            String message = "Amount is invalid";

            // Act
            InvalidLoanAmountException exception = new InvalidLoanAmountException(message);

            // Assert
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
            assertInstanceOf(RuntimeException.class, exception);
        }

        @Test
        @DisplayName("InvalidLoanAmountException should handle null message")
        void invalidLoanAmountException_withNullMessage() {
            // Act
            InvalidLoanAmountException exception = new InvalidLoanAmountException(null);

            // Assert
            assertNull(exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("LoanTypeNotFoundException should store message correctly")
        void loanTypeNotFoundException_withMessage() {
            // Arrange
            String message = "Loan type not found";

            // Act
            LoanTypeNotFoundException exception = new LoanTypeNotFoundException(message);

            // Assert
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
            assertInstanceOf(RuntimeException.class, exception);
        }

        @Test
        @DisplayName("LoanTypeNotFoundException should handle empty message")
        void loanTypeNotFoundException_withEmptyMessage() {
            // Act
            LoanTypeNotFoundException exception = new LoanTypeNotFoundException("");

            // Assert
            assertEquals("", exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("LoanApplicationNotFoundException should store message correctly")
        void loanApplicationNotFoundException_withMessage() {
            // Arrange
            String message = "Loan application not found";

            // Act
            LoanApplicationNotFoundException exception = new LoanApplicationNotFoundException(message);

            // Assert
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
            assertInstanceOf(RuntimeException.class, exception);
        }

        @Test
        @DisplayName("LoanApplicationNotFoundException should handle long message")
        void loanApplicationNotFoundException_withLongMessage() {
            // Arrange
            String message = "This is a very long message to test if the exception can handle long messages properly without any issues";

            // Act
            LoanApplicationNotFoundException exception = new LoanApplicationNotFoundException(message);

            // Assert
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("StateNotFoundException should store message correctly")
        void stateNotFoundException_withMessage() {
            // Arrange
            String message = "State not found";

            // Act
            StateNotFoundException exception = new StateNotFoundException(message);

            // Assert
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
            assertInstanceOf(RuntimeException.class, exception);
        }

        @Test
        @DisplayName("StateNotFoundException should handle special characters in message")
        void stateNotFoundException_withSpecialCharacters() {
            // Arrange
            String message = "State not found: ID=123, Name='Test State'";

            // Act
            StateNotFoundException exception = new StateNotFoundException(message);

            // Assert
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
        }
    }

    @Nested
    @DisplayName("Security Exceptions")
    class SecurityExceptionsTest {

        @Test
        @DisplayName("ExpiredJwtTokenException should store message correctly")
        void expiredJwtTokenException_withMessage() {
            // Arrange
            String message = "JWT token has expired";

            // Act
            ExpiredJwtTokenException exception = new ExpiredJwtTokenException(message);

            // Assert
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
            assertInstanceOf(RuntimeException.class, exception);
        }

        @Test
        @DisplayName("ExpiredJwtTokenException should handle null message")
        void expiredJwtTokenException_withNullMessage() {
            // Act
            ExpiredJwtTokenException exception = new ExpiredJwtTokenException(null);

            // Assert
            assertNull(exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("InsufficientPrivilegesException should store message correctly")
        void insufficientPrivilegesException_withMessage() {
            // Arrange
            String message = "User does not have required privileges";

            // Act
            InsufficientPrivilegesException exception = new InsufficientPrivilegesException(message);

            // Assert
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
            assertInstanceOf(RuntimeException.class, exception);
        }

        @Test
        @DisplayName("InsufficientPrivilegesException should handle empty message")
        void insufficientPrivilegesException_withEmptyMessage() {
            // Act
            InsufficientPrivilegesException exception = new InsufficientPrivilegesException("");

            // Assert
            assertEquals("", exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("InvalidJwtTokenException should store message correctly")
        void invalidJwtTokenException_withMessage() {
            // Arrange
            String message = "Invalid JWT token format";

            // Act
            InvalidJwtTokenException exception = new InvalidJwtTokenException(message);

            // Assert
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
            assertInstanceOf(RuntimeException.class, exception);
        }

        @Test
        @DisplayName("InvalidJwtTokenException should handle long message")
        void invalidJwtTokenException_withLongMessage() {
            // Arrange
            String message = "Invalid JWT token: The token provided does not conform to the expected JWT format and cannot be parsed";

            // Act
            InvalidJwtTokenException exception = new InvalidJwtTokenException(message);

            // Assert
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("MissingAuthorizationHeaderException should store message correctly")
        void missingAuthorizationHeaderException_withMessage() {
            // Arrange
            String message = "Authorization header is missing";

            // Act
            MissingAuthorizationHeaderException exception = new MissingAuthorizationHeaderException(message);

            // Assert
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
            assertInstanceOf(RuntimeException.class, exception);
        }

        @Test
        @DisplayName("MissingAuthorizationHeaderException should handle special characters")
        void missingAuthorizationHeaderException_withSpecialCharacters() {
            // Arrange
            String message = "Authorization header is missing from request: Bearer token required";

            // Act
            MissingAuthorizationHeaderException exception = new MissingAuthorizationHeaderException(message);

            // Assert
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("UserIdMismatchException should store message correctly")
        void userIdMismatchException_withMessage() {
            // Arrange
            String message = "User ID in token does not match requested user ID";

            // Act
            UserIdMismatchException exception = new UserIdMismatchException(message);

            // Assert
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
            assertInstanceOf(RuntimeException.class, exception);
        }

        @Test
        @DisplayName("UserIdMismatchException should handle null message")
        void userIdMismatchException_withNullMessage() {
            // Act
            UserIdMismatchException exception = new UserIdMismatchException(null);

            // Assert
            assertNull(exception.getMessage());
            assertNull(exception.getCause());
        }
    }
}
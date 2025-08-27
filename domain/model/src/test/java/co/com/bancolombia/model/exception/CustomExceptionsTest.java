package co.com.bancolombia.model.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomExceptionsTest {

    @Test
    void invalidLoanAmountException_withMessage() {
        // Arrange
        String message = "Amount is invalid";

        // Act
        InvalidLoanAmountException exception = new InvalidLoanAmountException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void loanTypeNotFoundException_withMessage() {
        // Arrange
        String message = "Loan type not found";

        // Act
        LoanTypeNotFoundException exception = new LoanTypeNotFoundException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void loanApplicationNotFoundException_withMessage() {
        // Arrange
        String message = "Loan application not found";

        // Act
        LoanApplicationNotFoundException exception = new LoanApplicationNotFoundException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void stateNotFoundException_withMessage() {
        // Arrange
        String message = "State not found";

        // Act
        StateNotFoundException exception = new StateNotFoundException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

}
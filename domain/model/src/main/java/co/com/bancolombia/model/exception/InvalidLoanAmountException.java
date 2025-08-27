package co.com.bancolombia.model.exception;

public class InvalidLoanAmountException extends RuntimeException {

    public InvalidLoanAmountException(String message) {
        super(message);
    }

    public InvalidLoanAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
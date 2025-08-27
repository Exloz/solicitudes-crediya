package co.com.bancolombia.model.exception;

public class LoanTypeNotFoundException extends RuntimeException {

    public LoanTypeNotFoundException(String message) {
        super(message);
    }

    public LoanTypeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
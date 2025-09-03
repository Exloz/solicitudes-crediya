package co.com.bancolombia.model.exception;

public class LoanApplicationNotFoundException extends RuntimeException {

    public LoanApplicationNotFoundException(String message) {
        super(message);
    }
}
package co.com.bancolombia.model.exception.business;

public class LoanApplicationNotFoundException extends RuntimeException {

    public LoanApplicationNotFoundException(String message) {
        super(message);
    }
}
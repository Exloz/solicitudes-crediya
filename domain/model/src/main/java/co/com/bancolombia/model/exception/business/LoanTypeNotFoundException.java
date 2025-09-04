package co.com.bancolombia.model.exception.business;

public class LoanTypeNotFoundException extends RuntimeException {

    public LoanTypeNotFoundException(String message) {
        super(message);
    }
}
package co.com.bancolombia.model.exception.business;

public class InvalidLoanAmountException extends RuntimeException {

    public InvalidLoanAmountException(String message) {
        super(message);
    }
}
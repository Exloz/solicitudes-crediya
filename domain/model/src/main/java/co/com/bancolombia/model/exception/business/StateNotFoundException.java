package co.com.bancolombia.model.exception.business;

public class StateNotFoundException extends RuntimeException {

    public StateNotFoundException(String message) {
        super(message);
    }
}
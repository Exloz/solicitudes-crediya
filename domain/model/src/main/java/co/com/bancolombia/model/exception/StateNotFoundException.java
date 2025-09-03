package co.com.bancolombia.model.exception;

public class StateNotFoundException extends RuntimeException {

    public StateNotFoundException(String message) {
        super(message);
    }
}
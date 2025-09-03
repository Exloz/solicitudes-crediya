package co.com.bancolombia.model.exception.security;

public class UserIdMismatchException extends RuntimeException {
    public UserIdMismatchException(String message) {
        super(message);
    }
}
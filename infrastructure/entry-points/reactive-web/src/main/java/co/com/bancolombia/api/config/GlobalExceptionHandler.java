package co.com.bancolombia.api.config;

import co.com.bancolombia.model.exception.InvalidLoanAmountException;
import co.com.bancolombia.model.exception.LoanApplicationNotFoundException;
import co.com.bancolombia.model.exception.LoanTypeNotFoundException;
import co.com.bancolombia.model.exception.StateNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GlobalExceptionHandler {

    public static Mono<ServerResponse> handleException(Throwable throwable) {
        log.error("Exception: {}", throwable.getMessage());

        return switch (throwable) {
            case InvalidLoanAmountException ignored -> handleBadRequest(throwable.getMessage());
            case LoanTypeNotFoundException ignored -> handleBadRequest(throwable.getMessage());
            case StateNotFoundException ignored -> handleBadRequest(throwable.getMessage());
            case LoanApplicationNotFoundException ignored ->
                    handleNotFound(throwable.getMessage());
            case IllegalArgumentException ignored -> handleBadRequest(throwable.getMessage());
            case WebExchangeBindException webExchangeBindException ->
                    handleValidationException(webExchangeBindException);
            default -> handleInternalServerError(throwable.getMessage());
        };
    }

    private static Mono<ServerResponse> handleBadRequest(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Bad Request");
        error.put("message", message);
        error.put("status", HttpStatus.BAD_REQUEST.value());

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }

    private static Mono<ServerResponse> handleValidationException(WebExchangeBindException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("error", "Validation Error");
        errors.put("status", HttpStatus.BAD_REQUEST.value());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage()));

        errors.put("fieldErrors", fieldErrors);

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errors);
    }

    private static Mono<ServerResponse> handleNotFound(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Not Found");
        error.put("message", message);
        error.put("status", HttpStatus.NOT_FOUND.value());

        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }

    private static Mono<ServerResponse> handleInternalServerError(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred");
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }
}
package co.com.bancolombia.api.config;

import co.com.bancolombia.model.exception.business.InvalidLoanAmountException;
import co.com.bancolombia.model.exception.business.LoanApplicationNotFoundException;
import co.com.bancolombia.model.exception.business.LoanTypeNotFoundException;
import co.com.bancolombia.model.exception.business.StateNotFoundException;
import co.com.bancolombia.model.exception.security.ExpiredJwtTokenException;
import co.com.bancolombia.model.exception.security.InsufficientPrivilegesException;
import co.com.bancolombia.model.exception.security.InvalidJwtTokenException;
import co.com.bancolombia.model.exception.security.MissingAuthorizationHeaderException;
import co.com.bancolombia.model.exception.security.UserIdMismatchException;
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

    private static final String EXCEPTION_LOG_MESSAGE = "Exception: {}";
    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";
    private static final String STATUS_KEY = "status";
    private static final String BAD_REQUEST_ERROR = "Bad Request";
    private static final String VALIDATION_ERROR = "Validation Error";
    private static final String FIELD_ERRORS_KEY = "fieldErrors";
    private static final String NOT_FOUND_ERROR = "Not Found";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "An unexpected error occurred";
    private static final String UNAUTHORIZED_ERROR = "Unauthorized";
    private static final String FORBIDDEN_ERROR = "Forbidden";

    public static Mono<ServerResponse> handleException(Throwable throwable) {
        log.error(EXCEPTION_LOG_MESSAGE, throwable.getMessage());

        return switch (throwable) {
            case InvalidJwtTokenException ignored -> handleUnauthorized(throwable.getMessage());
            case ExpiredJwtTokenException ignored -> handleUnauthorized(throwable.getMessage());
            case MissingAuthorizationHeaderException ignored -> handleUnauthorized(throwable.getMessage());
            case InsufficientPrivilegesException ignored -> handleForbidden(throwable.getMessage());
            case UserIdMismatchException ignored -> handleForbidden(throwable.getMessage());
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
        error.put(ERROR_KEY, BAD_REQUEST_ERROR);
        error.put(MESSAGE_KEY, message);
        error.put(STATUS_KEY, HttpStatus.BAD_REQUEST.value());

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }

    private static Mono<ServerResponse> handleValidationException(WebExchangeBindException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put(ERROR_KEY, VALIDATION_ERROR);
        errors.put(STATUS_KEY, HttpStatus.BAD_REQUEST.value());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage()));

        errors.put(FIELD_ERRORS_KEY, fieldErrors);

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errors);
    }

    private static Mono<ServerResponse> handleNotFound(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put(ERROR_KEY, NOT_FOUND_ERROR);
        error.put(MESSAGE_KEY, message);
        error.put(STATUS_KEY, HttpStatus.NOT_FOUND.value());

        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }

    private static Mono<ServerResponse> handleUnauthorized(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put(ERROR_KEY, UNAUTHORIZED_ERROR);
        error.put(MESSAGE_KEY, message);
        error.put(STATUS_KEY, HttpStatus.UNAUTHORIZED.value());

        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }

    private static Mono<ServerResponse> handleForbidden(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put(ERROR_KEY, FORBIDDEN_ERROR);
        error.put(MESSAGE_KEY, message);
        error.put(STATUS_KEY, HttpStatus.FORBIDDEN.value());

        return ServerResponse.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }

    private static Mono<ServerResponse> handleInternalServerError(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put(ERROR_KEY, INTERNAL_SERVER_ERROR);
        error.put(MESSAGE_KEY, INTERNAL_SERVER_ERROR_MESSAGE);
        error.put(STATUS_KEY, HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }
}
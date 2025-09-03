package co.com.bancolombia.api;

import co.com.bancolombia.api.config.GlobalExceptionHandler;
import co.com.bancolombia.api.dto.LoanApplicationRequest;
import co.com.bancolombia.api.mapper.LoanApplicationMapper;
import co.com.bancolombia.usecase.loanapplication.LoanApplicationUseCasePort;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private static final String RECEIVED_LOAN_APPLICATION_LOG = "Received loan application request: {}";
    private static final String LOAN_APPLICATION_REGISTERED_LOG = "Loan application registered successfully";
    private static final String ERROR_REGISTERING_LOAN_APPLICATION_LOG = "Error registering loan application on: {}";
    private static final String VALIDATION_ERRORS_PREFIX = "Validation errors: ";
    private static final String VALIDATION_ERROR_SEPARATOR = "; ";
    private static final String UNKNOWN_ORIGIN = "Unknown origin";
    private static final String CLASS_METHOD_LINE_FORMAT = "%s.%s (line %d)";

    private final LoanApplicationUseCasePort loanApplicationUseCase;
    private final Validator validator;
    private final LoanApplicationMapper mapper;

    public Mono<ServerResponse> registerLoanApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanApplicationRequest.class)
                .doOnNext(request -> log.info(RECEIVED_LOAN_APPLICATION_LOG, request))
                .flatMap(this::validateRequest)
                .map(mapper::toModel)
                .flatMap(loanApplicationUseCase::registerLoanApplication)
                .map(mapper::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .doOnSuccess(response -> log.info(LOAN_APPLICATION_REGISTERED_LOG))
                .doOnError(error -> log.error(ERROR_REGISTERING_LOAN_APPLICATION_LOG, getOriginOfError(error)))
                .onErrorResume(GlobalExceptionHandler::handleException);
    }

    private Mono<LoanApplicationRequest> validateRequest(LoanApplicationRequest request) {
        Set<ConstraintViolation<LoanApplicationRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder(VALIDATION_ERRORS_PREFIX);
            violations.forEach(violation -> message.append(violation.getMessage()).append(VALIDATION_ERROR_SEPARATOR));
            return Mono.error(new IllegalArgumentException(message.toString()));
        }
        return Mono.just(request);
    }

    private String getOriginOfError(Throwable error) {
        if (error.getStackTrace().length > 0) {
            var origin = error.getStackTrace()[0];
            return String.format(CLASS_METHOD_LINE_FORMAT, origin.getClassName(), origin.getMethodName(), origin.getLineNumber());
        }
        return UNKNOWN_ORIGIN;
    }
}

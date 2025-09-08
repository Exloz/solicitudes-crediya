package co.com.bancolombia.api;

import co.com.bancolombia.api.config.api.GlobalExceptionHandler;
import co.com.bancolombia.api.dto.LoanApplicationRequest;
import co.com.bancolombia.api.mapper.LoanApplicationMapper;
import co.com.bancolombia.consumer.service.AuthorizationService;
import co.com.bancolombia.model.exception.security.MissingAuthorizationHeaderException;
import co.com.bancolombia.usecase.loanapplication.LoanApplicationUseCasePort;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
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
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String MISSING_AUTHORIZATION_HEADER = "Missing Authorization header";
    private static final String INVALID_AUTHORIZATION_HEADER = "Invalid Authorization header format";

    private static final String RECEIVED_REVIEW_REQUEST_LOG = "Received review request with page: {}, size: {}, types: {}";
    private static final String REVIEW_REQUEST_PROCESSED_LOG = "Review request processed successfully";
    private static final String ERROR_PROCESSING_REVIEW_LOG = "Error processing review request on: {}";
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "10";
    private static final int MAX_PAGE_SIZE = 100;

    private final LoanApplicationUseCasePort loanApplicationUseCase;
    private final Validator validator;
    private final LoanApplicationMapper mapper;
    private final AuthorizationService authorizationService;

    public Mono<ServerResponse> registerLoanApplication(ServerRequest serverRequest) {
        return Mono.fromCallable(() -> extractJwtToken(serverRequest))
                .flatMap(jwtToken -> serverRequest.bodyToMono(LoanApplicationRequest.class)
                        .doOnNext(request -> log.info(RECEIVED_LOAN_APPLICATION_LOG, request))
                        .flatMap(this::validateRequest)
                        .map(mapper::toModel)
                        .flatMap(loanApplication -> loanApplicationUseCase.registerLoanApplication(loanApplication, jwtToken))
                        .map(mapper::toResponse)
                        .flatMap(response -> ServerResponse.ok().bodyValue(response))
                        .doOnSuccess(response -> log.info(LOAN_APPLICATION_REGISTERED_LOG))
                        .doOnError(error -> log.error(ERROR_REGISTERING_LOAN_APPLICATION_LOG, getOriginOfError(error)))
                )
                .onErrorResume(GlobalExceptionHandler::handleException);
    }

    public Mono<ServerResponse> getLoanApplicationsForReview(ServerRequest serverRequest) {
        return Mono.fromCallable(() -> extractJwtToken(serverRequest))
                .flatMap(this::validateJwtAndRole)
                .flatMap(validToken -> processReviewRequest(serverRequest, validToken))
                .onErrorResume(GlobalExceptionHandler::handleException);
    }

    private Mono<String> validateJwtAndRole(String jwtToken) {
        return authorizationService.validateAdvisorOrAdminAccess(jwtToken)
                .thenReturn(jwtToken);
    }

    private Mono<ServerResponse> processReviewRequest(ServerRequest serverRequest, String jwtToken) {
        var pagination = extractQueryParameters(serverRequest);

        log.info(RECEIVED_REVIEW_REQUEST_LOG, pagination.page(), pagination.size(), pagination.typeList);

        return loanApplicationUseCase.getLoanApplications(jwtToken, pagination.page(), pagination.size(), pagination.typeList())
                .map(mapper::toReviewResponse)
                .collectList()
                .flatMap(applications -> ServerResponse.ok().bodyValue(applications))
                .doOnSuccess(response -> log.info(REVIEW_REQUEST_PROCESSED_LOG))
                .doOnError(error -> log.error(ERROR_PROCESSING_REVIEW_LOG, getOriginOfError(error)));
    }

    private QueryParameters extractQueryParameters(ServerRequest serverRequest) {
        int page = Integer.parseInt(serverRequest.queryParam("page").orElse(DEFAULT_PAGE));
        int size = Integer.parseInt(serverRequest.queryParam("size").orElse(DEFAULT_SIZE));
        List<Integer> typeList = Arrays.stream(serverRequest.queryParam("type").orElse("1").split(","))
                .filter(s -> !s.isBlank())
                .map(Integer::parseInt)
                .toList();

        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }
        if (page < 0) {
            page = 0;
        }
        return new QueryParameters(page, size, typeList);
    }

    private record QueryParameters(int page, int size, List<Integer> typeList) {}

    private Mono<LoanApplicationRequest> validateRequest(LoanApplicationRequest request) {
        Set<ConstraintViolation<LoanApplicationRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder(VALIDATION_ERRORS_PREFIX);
            violations.forEach(violation -> message.append(violation.getMessage()).append(VALIDATION_ERROR_SEPARATOR));
            return Mono.error(new IllegalArgumentException(message.toString()));
        }
        return Mono.just(request);
    }

    private String extractJwtToken(ServerRequest serverRequest) {
        String authHeader = serverRequest.headers().firstHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || authHeader.trim().isEmpty()) {
            throw new MissingAuthorizationHeaderException(MISSING_AUTHORIZATION_HEADER);
        }

        if (!authHeader.startsWith(BEARER_PREFIX)) {
            throw new MissingAuthorizationHeaderException(INVALID_AUTHORIZATION_HEADER);
        }

        return authHeader.substring(BEARER_PREFIX.length());
    }

    private String getOriginOfError(Throwable error) {
        if (error.getStackTrace().length > 0) {
            var origin = error.getStackTrace()[0];
            return String.format(CLASS_METHOD_LINE_FORMAT, origin.getClassName(), origin.getMethodName(), origin.getLineNumber());
        }
        return UNKNOWN_ORIGIN;
    }
}

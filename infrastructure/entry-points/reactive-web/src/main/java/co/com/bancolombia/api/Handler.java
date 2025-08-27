package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.LoanApplicationRequest;
import co.com.bancolombia.api.dto.LoanApplicationResponse;
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

    private final LoanApplicationUseCasePort loanApplicationUseCase;
    private final Validator validator;
    private final LoanApplicationMapper mapper;

    public Mono<ServerResponse> registerLoanApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanApplicationRequest.class)
                .doOnNext(request -> log.info("Received loan application request: {}", request))
                .flatMap(this::validateRequest)
                .map(mapper::toModel)
                .flatMap(loanApplicationUseCase::registerLoanApplication)
                .map(mapper::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .doOnSuccess(response -> log.info("Loan application registered successfully"))
                .doOnError(error -> log.error("Error registering loan application", error));
    }

    private Mono<LoanApplicationRequest> validateRequest(LoanApplicationRequest request) {
        Set<ConstraintViolation<LoanApplicationRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder("Validation errors: ");
            violations.forEach(violation -> message.append(violation.getMessage()).append("; "));
            return Mono.error(new IllegalArgumentException(message.toString()));
        }
        return Mono.just(request);
    }
}

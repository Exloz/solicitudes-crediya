package co.com.bancolombia.model.loanapplication.gateways;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LoanApplicationRepository {

    Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication);

    Mono<LoanApplication> findById(UUID id);

    Flux<LoanApplication> findByClientId(String clientId);
}

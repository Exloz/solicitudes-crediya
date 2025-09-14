package co.com.bancolombia.usecase.debtcapacity;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

public interface DebtCapacityUseCasePort {
    Mono<String> calculateDebtCapacity(LoanApplication loanApplication, String jwtToken);
    Mono<Void> processDebtCapacityResponse(Object response);
}

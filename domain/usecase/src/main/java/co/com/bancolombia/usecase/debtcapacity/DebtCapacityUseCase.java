package co.com.bancolombia.usecase.debtcapacity;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.DebtCapacityQueueGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DebtCapacityUseCase implements DebtCapacityUseCasePort {

    private final DebtCapacityQueueGateway debtCapacityQueueGateway;

    @Override
    public Mono<String> calculateDebtCapacity(LoanApplication loanApplication, String jwtToken) {
        return debtCapacityQueueGateway.sendDebtCapacityRequest(loanApplication, jwtToken);
    }

    @Override
    public Mono<Void> processDebtCapacityResponse(Object response) {
        // This will be implemented when we handle the SQS response
        return Mono.empty();
    }
}

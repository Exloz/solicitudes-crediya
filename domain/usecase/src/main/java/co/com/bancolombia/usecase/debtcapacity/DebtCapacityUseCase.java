package co.com.bancolombia.usecase.debtcapacity;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.DebtCapacityQueueGateway;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.user.UserDebtCapacity;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DebtCapacityUseCase implements DebtCapacityUseCasePort {

    public static final String APPROVED = "Approved";
    public static final String REJECTED = "Rejected";
    public static final String MANUAL_REVIEW = "Manual review";

    private final DebtCapacityQueueGateway debtCapacityQueueGateway;
    private final LoanApplicationRepository loanApplicationRepository;

    @Override
    public Mono<String> calculateDebtCapacity(LoanApplication loanApplication, String jwtToken) {
        return debtCapacityQueueGateway.sendDebtCapacityRequest(loanApplication, jwtToken);
    }

    //TODO: Review if all infotmation is needed in the response
    @Override
    public Mono<Void> processDebtCapacityResponse(Object response) {
        if (!(response instanceof UserDebtCapacity debtResponse)) {
            return Mono.error(new IllegalArgumentException("Invalid response type"));
        }

        if (debtResponse.getApplicationId() == null) {
            return Mono.error(new IllegalArgumentException("Application ID cannot be null"));
        }

        if (debtResponse.getDecision() == null || debtResponse.getDecision().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Decision cannot be null or empty"));
        }

        Long statusId = determineStatusFromDecision(debtResponse.getDecision());

        return loanApplicationRepository.updateStatus(debtResponse.getApplicationId(), statusId)
                .then();
    }

    private Long determineStatusFromDecision(String decision) {
        return switch (decision) {
            case APPROVED -> 3L;
            case REJECTED -> 4L;
            case MANUAL_REVIEW -> 2L;
            default -> 1L;
        };
    }
}

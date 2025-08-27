package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.exception.InvalidLoanAmountException;
import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.exception.LoanTypeNotFoundException;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.state.State;
import co.com.bancolombia.model.exception.StateNotFoundException;
import co.com.bancolombia.model.state.gateways.StateRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class LoanApplicationUseCase implements LoanApplicationUseCasePort {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StateRepository stateRepository;

    @Override
    public Mono<LoanApplication> registerLoanApplication(LoanApplication loanApplication) {
        return loanTypeRepository.findById(loanApplication.getLoanTypeId())
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException("Loan type not found: " + loanApplication.getClientId())))
                .flatMap(loanType -> validateLoanAmount(loanApplication.getAmount(), loanType))
                .flatMap(loanType -> getPendingReviewState()
                        .map(state -> LoanApplication.builder()
                                .clientId(loanApplication.getClientId())
                                .amount(loanApplication.getAmount())
                                .term(loanApplication.getTerm())
                                .loanTypeId(loanApplication.getLoanTypeId())
                                .status(state.getId())
                                .createdAt(LocalDateTime.now())
                                .build()))
                .flatMap(loanApplicationRepository::saveLoanApplication);
    }

    private Mono<LoanType> validateLoanAmount(BigDecimal amount, LoanType loanType) {
        if (amount.compareTo(loanType.getMinAmount()) < 0) {
            return Mono.error(new InvalidLoanAmountException(
                String.format("Amount %.2f is below minimum %.2f for loan type %s",
                    amount, loanType.getMinAmount(), loanType.getName())));
        }
        if (amount.compareTo(loanType.getMaxAmount()) > 0) {
            return Mono.error(new InvalidLoanAmountException(
                String.format("Amount %.2f exceeds maximum %.2f for loan type %s",
                    amount, loanType.getMaxAmount(), loanType.getName())));
        }
        return Mono.just(loanType);
    }

    private Mono<State> getPendingReviewState() {
        return stateRepository.findByName("Pending review")
                .switchIfEmpty(Mono.error(new StateNotFoundException("Pending review state not found")));
    }

}

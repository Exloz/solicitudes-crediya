package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.user.UserValidator;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.exception.business.InvalidLoanAmountException;
import co.com.bancolombia.model.exception.business.LoanTypeNotFoundException;
import co.com.bancolombia.model.exception.business.StateNotFoundException;
import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.state.State;
import co.com.bancolombia.model.state.gateways.StateRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class LoanApplicationUseCase implements LoanApplicationUseCasePort {

    private static final String LOAN_TYPE_NOT_FOUND_MESSAGE = "Loan type not found: ";
    private static final String AMOUNT_BELOW_MINIMUM_MESSAGE = "Amount %.2f is below minimum %.2f for loan type %s";
    private static final String AMOUNT_EXCEEDS_MAXIMUM_MESSAGE = "Amount %.2f exceeds maximum %.2f for loan type %s";
    private static final String PENDING_REVIEW_STATE_NAME = "Pending review";
    private static final String PENDING_REVIEW_STATE_NOT_FOUND_MESSAGE = "Pending review state not found";

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StateRepository stateRepository;
    private final UserValidator userValidator;

    @Override
    public Mono<LoanApplication> registerLoanApplication(LoanApplication loanApplication, String jwtToken) {
        // First validate that the user exists and the token is valid
        return userValidator.validateUserExists(loanApplication.getClientId(), jwtToken)
                .flatMap(userInfo -> {
                    // User exists, continue with loan application logic
                    return loanTypeRepository.findById(loanApplication.getLoanTypeId())
                            .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(LOAN_TYPE_NOT_FOUND_MESSAGE + loanApplication.getLoanTypeId())))
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
                });
    }

    private Mono<LoanType> validateLoanAmount(BigDecimal amount, LoanType loanType) {
        if (amount.compareTo(loanType.getMinAmount()) < 0) {
            return Mono.error(new InvalidLoanAmountException(
                String.format(AMOUNT_BELOW_MINIMUM_MESSAGE,
                    amount, loanType.getMinAmount(), loanType.getName())));
        }
        if (amount.compareTo(loanType.getMaxAmount()) > 0) {
            return Mono.error(new InvalidLoanAmountException(
                String.format(AMOUNT_EXCEEDS_MAXIMUM_MESSAGE,
                    amount, loanType.getMaxAmount(), loanType.getName())));
        }
        return Mono.just(loanType);
    }

    private Mono<State> getPendingReviewState() {
        return stateRepository.findByName(PENDING_REVIEW_STATE_NAME)
                .switchIfEmpty(Mono.error(new StateNotFoundException(PENDING_REVIEW_STATE_NOT_FOUND_MESSAGE)));
    }

}

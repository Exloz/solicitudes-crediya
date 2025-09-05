package co.com.bancolombia.usecase.loanapplication;


import co.com.bancolombia.model.loanapplication.LoanApplicationReview;
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
import co.com.bancolombia.model.user.UserInfo;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class LoanApplicationUseCase implements LoanApplicationUseCasePort {

    // Error messages
    private static final String LOAN_TYPE_NOT_FOUND_MESSAGE = "Loan type not found: ";
    private static final String AMOUNT_BELOW_MINIMUM_MESSAGE = "Amount %.2f is below minimum %.2f for loan type %s";
    private static final String AMOUNT_EXCEEDS_MAXIMUM_MESSAGE = "Amount %.2f exceeds maximum %.2f for loan type %s";
    private static final String PENDING_REVIEW_STATE_NAME = "Pending review";
    private static final String PENDING_REVIEW_STATE_NOT_FOUND_MESSAGE = "Pending review state not found";
    private static final String STATE_NOT_FOUND_MESSAGE = "State not found: ";

    // Review status names
    private static final String PENDING_REVIEW_STATUS = "Pending review";
    private static final List<String> REVIEW_STATUSES = List.of(PENDING_REVIEW_STATUS);

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StateRepository stateRepository;
    private final UserValidator userValidator;

    @Override
    public Mono<LoanApplication> registerLoanApplication(LoanApplication loanApplication, String jwtToken) {
        return userValidator.validateUserExists(loanApplication.getClientId(), jwtToken)
                .flatMap(userInfo -> loanTypeRepository.findById(loanApplication.getLoanTypeId())
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
                        .flatMap(loanApplicationRepository::saveLoanApplication));
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

    @Override
    public Flux<LoanApplicationReview> getLoanApplicationsForReview(String jwtToken, int page, int size) {
        return loanApplicationRepository.findByStatus(REVIEW_STATUSES.get(0), size, (long) page * size)
                .flatMap(application -> enrichLoanApplicationWithDetails(application, jwtToken));
    }

    @Override
    public Flux<LoanApplicationReview> getClientLoanApplications(String clientId, int page, int size, String jwtToken) {
        return userValidator.validateUserExists(clientId, jwtToken)
                .thenMany(loanApplicationRepository.findByClientId(clientId, size, (long) page * size))
                .flatMap(application -> enrichLoanApplicationWithDetails(application, jwtToken));
    }

    private Mono<LoanApplicationReview> enrichLoanApplicationWithDetails(LoanApplication application, String jwtToken) {
        return Mono.zip(
                getUserInfo(application.getClientId(), jwtToken),
                getLoanType(application.getLoanTypeId()),
                getState(application.getStatus()),
                getTotalMonthlyDebt(application.getClientId())
        ).map(tuple -> {
            UserInfo userInfo = tuple.getT1();
            LoanType loanType = tuple.getT2();
            State state = tuple.getT3();
            BigDecimal totalMonthlyDebt = tuple.getT4();

            return LoanApplicationReview.builder()
                    .id(application.getId())
                    .amount(application.getAmount())
                    .term(application.getTerm())
                    .loanApplication(application)
                    .loanType(loanType)
                    .state(state)
                    .userInfo(userInfo)
                    .totalMonthlyDebt(totalMonthlyDebt)
                    .createdAt(application.getCreatedAt())
                    .build();
        });
    }

    private Mono<UserInfo> getUserInfo(String clientId, String jwtToken) {
        return userValidator.validateUserExists(clientId, jwtToken);
    }

    private Mono<LoanType> getLoanType(Long loanTypeId) {
        return loanTypeRepository.findById(loanTypeId)
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(LOAN_TYPE_NOT_FOUND_MESSAGE + loanTypeId)));
    }

    private Mono<State> getState(Long stateId) {
        return stateRepository.findById(stateId)
                .switchIfEmpty(Mono.error(new StateNotFoundException(STATE_NOT_FOUND_MESSAGE + stateId)));
    }

    private Mono<BigDecimal> getTotalMonthlyDebt(String clientId) {
        return Mono.just(BigDecimal.valueOf(500.00));
    }

}

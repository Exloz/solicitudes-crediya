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
import co.com.bancolombia.model.loanapplication.gateways.NotificationGateway;
import co.com.bancolombia.model.state.State;
import co.com.bancolombia.model.state.gateways.StateRepository;
import co.com.bancolombia.model.user.UserInfo;
import co.com.bancolombia.model.user.gateways.AuthorizationGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class LoanApplicationUseCase implements LoanApplicationUseCasePort {

    private static final String LOAN_TYPE_NOT_FOUND_MESSAGE = "Loan type not found: ";
    private static final String AMOUNT_BELOW_MINIMUM_MESSAGE = "Amount %.2f is below minimum %.2f for loan type %s";
    private static final String AMOUNT_EXCEEDS_MAXIMUM_MESSAGE = "Amount %.2f exceeds maximum %.2f for loan type %s";
    private static final String STATE_NOT_FOUND_MESSAGE = "State not found: ";
    public static final Long PENDING_REVIEW_STATE_ID = 1L;

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StateRepository stateRepository;
    private final UserValidator userValidator;
    private final AuthorizationGateway authorizationGateway;
    private final NotificationGateway notificationGateway;

    @Override
    public Mono<LoanApplication> registerLoanApplication(LoanApplication loanApplication, String jwtToken) {
        return userValidator.validateUserIdMatch(loanApplication.getClientId(), jwtToken)
                .then(userValidator.validateUserInfo(loanApplication.getClientId(), jwtToken))
                .flatMap(userInfo -> loanTypeRepository.findById(loanApplication.getLoanTypeId())
                        .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(LOAN_TYPE_NOT_FOUND_MESSAGE + loanApplication.getLoanTypeId())))
                        .doOnNext(loanType -> validateLoanAmount(loanApplication.getAmount(), loanType))
                        .map(loanType -> LoanApplication.builder()
                                .clientId(loanApplication.getClientId())
                                .amount(loanApplication.getAmount())
                                .term(loanApplication.getTerm())
                                .loanTypeId(loanApplication.getLoanTypeId())
                                .statusId(PENDING_REVIEW_STATE_ID)
                                .createdAt(LocalDateTime.now())
                                .build()
                        )
                        .flatMap(loanApplicationRepository::saveLoanApplication)
                );
    }

    private void validateLoanAmount(BigDecimal amount, LoanType loanType) {
        if (amount.compareTo(loanType.getMinAmount()) < 0) {
            throw new InvalidLoanAmountException(
                    String.format(AMOUNT_BELOW_MINIMUM_MESSAGE,
                            amount, loanType.getMinAmount(), loanType.getName()));
        }
        if (amount.compareTo(loanType.getMaxAmount()) > 0) {
            throw new InvalidLoanAmountException(
                    String.format(AMOUNT_EXCEEDS_MAXIMUM_MESSAGE,
                            amount, loanType.getMaxAmount(), loanType.getName()));
        }
    }

    @Override
    public Flux<LoanApplicationReview> getLoanApplications(String jwtToken, int page, int size, List<Integer> typeList) {
        return loanApplicationRepository.findByStatus(typeList, size, (long) page * size)
                .flatMap(application -> enrichLoanApplicationWithDetails(application, jwtToken));
    }

    private Mono<LoanApplicationReview> enrichLoanApplicationWithDetails(LoanApplication loanApplication, String jwtToken) {
        return Mono.zip(
                getUserInfo(loanApplication.getClientId(), jwtToken),
                getLoanType(loanApplication.getLoanTypeId()),
                getState(loanApplication.getStatusId())
        ).map(tuple -> {
            UserInfo userInfo = tuple.getT1();
            LoanType loanType = tuple.getT2();
            State state = tuple.getT3();

            return LoanApplicationReview.builder()
                    .id(loanApplication.getId())
                    .amount(loanApplication.getAmount())
                    .term(loanApplication.getTerm())
                    .loanApplication(loanApplication)
                    .loanType(loanType)
                    .state(state)
                    .userInfo(userInfo)
                    .monthlyRequestAmount(getMonthlyRequestAmount(loanApplication, loanType))
                    .createdAt(loanApplication.getCreatedAt())
                    .build();
        });
    }

    private Mono<UserInfo> getUserInfo(String clientId, String jwtToken) {
        return userValidator.validateUserInfo(clientId, jwtToken);
    }

    private Mono<LoanType> getLoanType(Long loanTypeId) {
        return loanTypeRepository.findById(loanTypeId)
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(LOAN_TYPE_NOT_FOUND_MESSAGE + loanTypeId)));
    }

    private Mono<State> getState(Long stateId) {
        return stateRepository.findById(stateId)
                .switchIfEmpty(Mono.error(new StateNotFoundException(STATE_NOT_FOUND_MESSAGE + stateId)));
    }

    public BigDecimal getMonthlyRequestAmount(LoanApplication loanApplication, LoanType loanType) {
        BigDecimal principal = loanApplication.getAmount();
        BigDecimal monthlyRate = loanType.getInterestRate().divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        int term = loanApplication.getTerm();

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(term), 2, RoundingMode.HALF_UP);
        }

        BigDecimal rateToTerm = BigDecimal.ONE.add(monthlyRate).pow(term);
        BigDecimal numerator = monthlyRate.multiply(rateToTerm);
        BigDecimal denominator = rateToTerm.subtract(BigDecimal.ONE);

        return principal.multiply(numerator.divide(denominator, 10, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Mono<LoanApplication> updateLoanApplicationStatus(UUID id, Long statusId, String jwtToken) {
        return authorizationGateway.validateAdvisorOrAdminAccess(jwtToken)
                .then(loanApplicationRepository.findById(id))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Loan application not found: " + id)))
                .flatMap(application -> validateStatusTransition(application.getStatusId(), statusId)
                        .then(loanApplicationRepository.updateStatus(id, statusId)))
                .flatMap(updatedApplication -> notificationGateway.sendStatusChangeNotification(updatedApplication)
                        .thenReturn(updatedApplication));
    }

    private Mono<Void> validateStatusTransition(Long currentStatus, Long newStatus) {
        // Only allow transitions to Approved (3) or Rejected (4)
        if (newStatus != 3L && newStatus != 4L) {
            return Mono.error(new IllegalArgumentException("Invalid status transition. Only Approved (3) or Rejected (4) are allowed"));
        }

        return Mono.empty();
    }
}

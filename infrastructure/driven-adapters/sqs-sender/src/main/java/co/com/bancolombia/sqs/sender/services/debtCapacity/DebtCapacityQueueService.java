package co.com.bancolombia.sqs.sender.services.debtCapacity;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.DebtCapacityQueueGateway;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.user.UserInfo;
import co.com.bancolombia.model.user.UserValidator;
import co.com.bancolombia.sqs.sender.SQSSender;
import co.com.bancolombia.sqs.sender.services.QueueType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class DebtCapacityQueueService implements DebtCapacityQueueGateway {

    private final SQSSender sqsSender;
    private final ObjectMapper objectMapper;
    private final UserValidator userValidator;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    static final Integer APROVED_STATE = 3;

    @Override
    public Mono<String> sendDebtCapacityRequest(LoanApplication loanApplication, String jwtToken) {
        return userValidator.validateUserInfo(loanApplication.getClientId(), jwtToken)
                .zipWith(loanTypeRepository.findById(loanApplication.getLoanTypeId()))
                .zipWith(calculateTotalMonthlyDebt(loanApplication.getClientId()))
                .flatMap(tuple -> {
                    UserInfo userInfo = tuple.getT1().getT1();
                    LoanType loanType = tuple.getT1().getT2();
                    BigDecimal totalMonthlyDebt = tuple.getT2();
                    return send(loanApplication, userInfo, loanType, totalMonthlyDebt);
                });
    }

    private Mono<String> send(LoanApplication loanApplication, UserInfo userInfo, LoanType loanType, BigDecimal totalMonthlyDebt) {
        return Mono.fromCallable(() -> createDebtCapacityMessage(loanApplication, userInfo, loanType, totalMonthlyDebt))
                .flatMap(messageJson -> sqsSender.send(messageJson, QueueType.DEBT_REQUESTS))
                .doOnNext(messageId -> log.info("Debt capacity request sent to SQS for loan application {} with message ID: {}",
                        loanApplication.getId(), messageId))
                .doOnError(error -> log.error("Failed to send debt capacity request for loan application {}: {}",
                        loanApplication.getId(), error.getMessage()));
    }

    @Override
    public String createDebtCapacityMessage(LoanApplication application, UserInfo userInfo, LoanType loanType, BigDecimal totalMonthlyDebt) {
        try {
            DebtCapacityReqMessage message = DebtCapacityReqMessage.builder()
                    .applicationId(application.getId())
                    .clientId(application.getClientId())
                    .clientEmail(userInfo.email())
                    .baseSalary(userInfo.baseSalary())
                    .loanAmount(application.getAmount())
                    .loanTerm(application.getTerm())
                    .loanTypeId(application.getLoanTypeId())
                    .interestRate(loanType.getInterestRate())
                    .totalMonthlyDebt(totalMonthlyDebt)
                    .requestDate(Instant.now())
                    .build();

            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize debt capacity request message for application {}", application.getId(), e);
            throw new RuntimeException("Failed to create debt capacity request message", e);
        }
    }

    private Mono<BigDecimal> calculateTotalMonthlyDebt(String clientId){
        return loanApplicationRepository.findByStatusAndClientId(APROVED_STATE, clientId)
                .map(loan -> loan.getAmount().divide(BigDecimal.valueOf(loan.getTerm()), 2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
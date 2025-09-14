package co.com.bancolombia.sqs.listener;

import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.sqs.sender.services.debtCapacity.DebtCapacityResponseMessage;
import co.com.bancolombia.usecase.debtcapacity.DebtCapacityUseCasePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final ObjectMapper objectMapper;
    private final DebtCapacityUseCasePort debtCapacityUseCase;
    private final LoanApplicationRepository loanApplicationRepository;

    @Override
    public Mono<Void> apply(Message message) {
        log.info("Received SQS message: {}", message.body());

        return Mono.fromCallable(() -> {
                    try {
                        return objectMapper.readValue(message.body(), DebtCapacityResponseMessage.class);
                    } catch (Exception e) {
                        log.error("Failed to deserialize SQS message: {}", e.getMessage());
                        throw new RuntimeException("Invalid message format", e);
                    }
                })
                .flatMap(this::processDebtCapacityResponse)
                .doOnError(error -> log.error("Failed to process SQS message: {}", error.getMessage()))
                .onErrorResume(error -> {
                    log.warn("Skipping malformed message and continuing processing");
                    return Mono.empty();
                });
    }

    private Mono<Void> processDebtCapacityResponse(DebtCapacityResponseMessage response) {
        log.info("Processing debt capacity response for application {} with decision: {}", response.getApplicationId(), response.getDecision());

        if (response.getApplicationId() == null) {
            log.error("Application ID is null in debt capacity response");
            return Mono.error(new IllegalArgumentException("Application ID cannot be null"));
        }

        if (response.getDecision() == null || response.getDecision().isEmpty()) {
            log.error("Decision is null or empty in debt capacity response for application {}", response.getApplicationId());
            return Mono.error(new IllegalArgumentException("Decision cannot be null or empty"));
        }

        Long statusId = determineStatusFromDecision(response.getDecision());

        return loanApplicationRepository.updateStatus(response.getApplicationId(), statusId)
                .doOnNext(updated -> log.info("Successfully updated loan application {} status to {} ({})",
                        response.getApplicationId(), statusId, response.getDecision()))
                .doOnError(error -> log.error("Failed to update loan application status for {}: {}",
                        response.getApplicationId(), error.getMessage()))
                .onErrorResume(error -> {
                    log.error("Critical error updating loan application status, manual intervention may be required: {}", error.getMessage());
                    return Mono.error(error);
                })
                .then();
    }

    private Long determineStatusFromDecision(String decision) {
        return switch (decision.toUpperCase()) {
            case "APROBADO" -> 3L; // Approved
            case "RECHAZADO" -> 4L; // Rejected
            case "REVISION MANUAL" -> 5L; // Manual review (assuming this is a new status)
            default -> 1L; // Default to pending review
        };
    }
}

package co.com.bancolombia.sqs.sender.services.loanApplication;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.NotificationQueueGateway;
import co.com.bancolombia.sqs.sender.SQSSender;
import co.com.bancolombia.sqs.sender.services.QueueType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanApplicationQueueService implements NotificationQueueGateway {

    private final SQSSender sqsSender;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<String> sendStatusNotification(LoanApplication loanApplication, String clientEmail) {
        return Mono.fromCallable(() -> createNotificationMessage(loanApplication, clientEmail))
                .flatMap((String message) -> sqsSender.send(message, QueueType.NOTIFICATIONS))
                .doOnNext(messageId -> log.info("Notification sent to SQS for loan application {} with message ID: {}",
                        loanApplication.getId(), messageId))
                .doOnError(error -> log.error("Failed to send notification for loan application {}: {}",
                        loanApplication.getId(), error.getMessage()));
    }

    @Override
    public String createNotificationMessage(LoanApplication application, String clientEmail) {
        try {
            NotificationMessage message = NotificationMessage.builder()
                    .applicationId(application.getId())
                    .clientId(application.getClientId())
                    .clientEmail(clientEmail)
                    .newStatus(application.getStatusId())
                    .decisionTimestamp(Instant.now())
                    .amount(application.getAmount())
                    .term(application.getTerm())
                    .loanTypeId(application.getLoanTypeId())
                    .build();

            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize notification message for application {}", application.getId(), e);
            throw new RuntimeException("Failed to create notification message", e);
        }
    }
}
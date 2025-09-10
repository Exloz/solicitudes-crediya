package co.com.bancolombia.sqs.sender;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.NotificationGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanApplicationNotificationService implements NotificationGateway {

    private final SQSSender sqsSender;
    private final ObjectMapper objectMapper;

    public Mono<String> sendStatusChangeNotification(LoanApplication application, String clientEmail) {
        return Mono.fromCallable(() -> createNotificationMessage(application, clientEmail))
                .flatMap(sqsSender::send)
                .doOnNext(messageId -> log.info("Notification sent to SQS for loan application {} with message ID: {}",
                        application.getId(), messageId))
                .doOnError(error -> log.error("Failed to send notification for loan application {}: {}",
                        application.getId(), error.getMessage()));
    }

    private String createNotificationMessage(LoanApplication application, String clientEmail) {
        try {
            NotificationMessage message = NotificationMessage.builder()
                    .applicationId(application.getId())
                    .clientId(application.getClientId())
                    .clientEmail(clientEmail)
                    .newStatus(application.getStatusId())
                    .decisionTimestamp(LocalDateTime.now())
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
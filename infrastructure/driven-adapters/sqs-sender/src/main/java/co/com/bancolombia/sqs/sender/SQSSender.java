package co.com.bancolombia.sqs.sender;

import co.com.bancolombia.sqs.sender.config.SQSSenderProperties;
import co.com.bancolombia.sqs.sender.services.QueueType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;

    public Mono<String> send(String message, QueueType queueType) {
        return Mono.fromCallable(() -> buildRequest(message, queueType))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.info("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message, QueueType queueType) {
        String queueUrl = switch (queueType) {
            case NOTIFICATIONS -> properties.queueNotificationsUrl();
            case DEBT_REQUESTS -> properties.queueDebtRequestsUrl();
        };

        return SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
    }
}

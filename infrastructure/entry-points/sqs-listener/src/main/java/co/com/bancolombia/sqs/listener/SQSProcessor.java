package co.com.bancolombia.sqs.listener;

import co.com.bancolombia.model.user.UserDebtCapacity;
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

    @Override
    public Mono<Void> apply(Message message) {
        log.info("Received SQS message: {}", message.body());

        return Mono.fromCallable(() -> {
                    try {
                        return objectMapper.readValue(message.body(), UserDebtCapacity.class);
                    } catch (Exception e) {
                        log.error("Failed to deserialize SQS message: {}", e.getMessage());
                        throw new RuntimeException("Invalid message format", e);
                    }
                })
                .flatMap(debtCapacityUseCase::processDebtCapacityResponse)
                .doOnError(error -> log.error("Failed to process SQS message: {}", error.getMessage()))
                .onErrorResume(error -> {
                    log.warn("Skipping malformed message and continuing processing");
                    return Mono.empty();
                });
    }
}

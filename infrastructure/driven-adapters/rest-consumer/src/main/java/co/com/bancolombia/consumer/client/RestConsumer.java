package co.com.bancolombia.consumer.client;

import co.com.bancolombia.consumer.dto.UserInfoRes;
import co.com.bancolombia.consumer.service.AuthorizationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestConsumer {
    private final WebClient client;
    private final AuthorizationService authorizationService;

    @CircuitBreaker(name = "getUserById")
    public Mono<UserInfoRes> getUserById(String userId, String jwtToken) {
        return authorizationService.validateTokenAndAnyRole(jwtToken, "USER", "ADVISOR")
            .then(authorizationService.validateToken(jwtToken))
            .flatMap(claims -> {
                log.info("Making request to get user info for userId: {}", userId);
                return client
                    .get()
                    .uri("/api/v1/usuarios/{userId}", userId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(UserInfoRes.class);
            });
    }
}

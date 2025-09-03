package co.com.bancolombia.consumer;

import co.com.bancolombia.model.exception.security.InsufficientPrivilegesException;
import co.com.bancolombia.model.exception.security.UserIdMismatchException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer /* implements Gateway from domain */{
    private final WebClient client;
    private final JwtService jwtService;

    @CircuitBreaker(name = "getUserById")
    public Mono<UserInfoRes> getUserByIdDocument(String userId, String jwtToken) {
        Claims claims = jwtService.validateToken(jwtToken);

        // Validar rol USER
        if (!jwtService.hasRequiredRole(claims, "USER")) {
            return Mono.error(new InsufficientPrivilegesException("User does not have required USER role"));
        }

        // Validar que el userId del token coincida con el solicitado
        if (!jwtService.validateUserIdMatch(claims, userId)) {
            return Mono.error(new UserIdMismatchException("User ID in token does not match requested user ID"));
        }

        return client
            .get()
            .uri("/api/v1/usuarios/{userId}", userId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .retrieve()
            .bodyToMono(UserInfoRes.class);
    }
}

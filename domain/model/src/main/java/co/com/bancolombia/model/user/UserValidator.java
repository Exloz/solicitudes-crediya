package co.com.bancolombia.model.user;

import reactor.core.publisher.Mono;

public interface UserValidator {

    Mono<UserInfo> validateUserExists(String userId, String jwtToken);
}
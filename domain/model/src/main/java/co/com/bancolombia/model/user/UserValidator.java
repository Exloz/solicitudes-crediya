package co.com.bancolombia.model.user;

import reactor.core.publisher.Mono;

public interface UserValidator {

    Mono<UserInfo> validateUserInfo(String userId, String jwtToken);
}
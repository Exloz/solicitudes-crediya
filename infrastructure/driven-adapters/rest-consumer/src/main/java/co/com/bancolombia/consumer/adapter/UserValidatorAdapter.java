package co.com.bancolombia.consumer.adapter;

import co.com.bancolombia.consumer.client.RestConsumer;
import co.com.bancolombia.consumer.dto.UserInfoRes;
import co.com.bancolombia.consumer.service.AuthorizationService;
import co.com.bancolombia.model.exception.security.UserIdMismatchException;
import co.com.bancolombia.model.user.UserInfo;
import co.com.bancolombia.model.user.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserValidatorAdapter implements UserValidator {

    private final RestConsumer restConsumer;
    private final AuthorizationService authorizationService;

    @Override
    public Mono<UserInfo> validateUserInfo(String userId, String jwtToken) {
        return restConsumer.getUserById(userId, jwtToken)
                .map(this::mapToUserInfo)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found - cannot create loan application")));
    }

    @Override
    public Mono<Void> validateUserIdMatch(String clientId, String jwtToken) {
        return authorizationService.extractUserId(jwtToken)
                .flatMap(tokenUserId -> {
                    if (!clientId.equals(tokenUserId.toString())) {
                        return Mono.error(new UserIdMismatchException("User ID in token does not match requested user ID"));
                    }
                    return Mono.empty();
                });
    }

    private UserInfo mapToUserInfo(UserInfoRes userInfoRes) {
        return new UserInfo(
            userInfoRes.userId(),
            userInfoRes.name(),
            userInfoRes.lastName(),
            userInfoRes.email(),
            userInfoRes.idDocument(),
            userInfoRes.phoneNumber(),
            userInfoRes.address(),
            userInfoRes.birthDate(),
            userInfoRes.roleId().toString(),
            userInfoRes.baseSalary()
        );
    }
}
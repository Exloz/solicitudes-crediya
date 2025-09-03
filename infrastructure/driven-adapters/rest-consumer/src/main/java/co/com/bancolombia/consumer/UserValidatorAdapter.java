package co.com.bancolombia.consumer;

import co.com.bancolombia.model.user.UserInfo;
import co.com.bancolombia.model.user.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserValidatorAdapter implements UserValidator {

    private final RestConsumer restConsumer;

    @Override
    public Mono<UserInfo> validateUserExists(String userId, String jwtToken) {
        return restConsumer.getUserByIdDocument(userId, jwtToken)
                .map(this::mapToUserInfo)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found - cannot create loan application")));
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
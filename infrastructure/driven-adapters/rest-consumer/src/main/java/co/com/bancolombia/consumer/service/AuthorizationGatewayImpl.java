package co.com.bancolombia.consumer.service;

import co.com.bancolombia.model.user.gateways.AuthorizationGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthorizationGatewayImpl implements AuthorizationGateway {

    private final AuthorizationService authorizationService;

    @Override
    public Mono<Void> validateAdvisorOrAdminAccess(String jwtToken) {
        return authorizationService.validateAdvisorOrAdminAccess(jwtToken);
    }
}
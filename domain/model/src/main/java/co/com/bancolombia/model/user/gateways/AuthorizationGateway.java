package co.com.bancolombia.model.user.gateways;

import reactor.core.publisher.Mono;

public interface AuthorizationGateway {

    Mono<Void> validateAdvisorOrAdminAccess(String jwtToken);
}
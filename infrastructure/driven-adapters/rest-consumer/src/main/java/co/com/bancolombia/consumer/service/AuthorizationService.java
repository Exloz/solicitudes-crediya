package co.com.bancolombia.consumer.service;

import co.com.bancolombia.consumer.enums.RoleId;
import co.com.bancolombia.model.exception.security.InsufficientPrivilegesException;
import co.com.bancolombia.model.exception.security.InvalidJwtTokenException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final JwtService jwtService;

    private static final String ERROR_ROLE_NOT_FOUND = "Role not found in JWT token";
    private static final String ERROR_USERID_NOT_FOUND = "User ID not found in JWT token";
    private static final String ERROR_REQUIRED_ROLE = "User does not have required role: %s";
    private static final String ERROR_REQUIRED_ANY_ROLE = "User does not have any of the required roles: %s";

    public Mono<Void> validateTokenAndRole(String jwtToken, String requiredRole) {
        return Mono.fromCallable(() -> {
                    log.info("Validating JWT token and role: {}", requiredRole);

                    Claims claims = jwtService.validateToken(jwtToken);

                    if (!jwtService.hasRequiredRole(claims, requiredRole)) {
                        String userRole = claims.get("role", String.class);
                        log.warn("Access denied. Required role: {}, User role: {}", requiredRole, userRole);
                        throw new InsufficientPrivilegesException(
                            String.format(ERROR_REQUIRED_ROLE, requiredRole));
                    }

                    log.debug("JWT token and role validation successful for role: {}", requiredRole);
                    return claims;
                })
                .doOnError(error -> log.error("JWT validation failed: {}", error.getMessage()))
                .then();
    }

    public Mono<Void> validateTokenAndAnyRole(String jwtToken, String... requiredRoles) {
        return Mono.fromCallable(() -> {
                    log.info("Validating JWT token and any of roles: {}", String.join(", ", requiredRoles));

                    Claims claims = jwtService.validateToken(jwtToken);

                    if (!jwtService.hasRequiredRole(claims, requiredRoles)) {
                        String userRole = claims.get("role", String.class);
                        log.warn("Access denied. Required roles: {}, User role: {}",
                                String.join(", ", requiredRoles), userRole);
                        throw new InsufficientPrivilegesException(
                            String.format(ERROR_REQUIRED_ANY_ROLE,
                                String.join(", ", requiredRoles)));
                    }

                    log.debug("JWT token and role validation successful for one of roles: {}",
                            String.join(", ", requiredRoles));
                    return claims;
                })
                .doOnError(error -> log.error("JWT validation failed: {}", error.getMessage()))
                .then();
    }

    public Mono<Claims> validateToken(String jwtToken) {
        return Mono.fromCallable(() -> {
                    log.debug("Validating JWT token only");
                    Claims claims = jwtService.validateToken(jwtToken);
                    log.debug("JWT token validation successful");
                    return claims;
                })
                .doOnError(error -> log.error("JWT validation failed: {}", error.getMessage()));
    }

    public Mono<Long> extractUserId(String jwtToken) {
        return validateToken(jwtToken)
                .map(claims -> {
                    Long userId = jwtService.getUserIdFromToken(claims);
                    if (userId == null) {
                        throw new InvalidJwtTokenException(ERROR_USERID_NOT_FOUND);
                    }
                    return userId;
                });
    }

    public Mono<String> extractUserRole(String jwtToken) {
        return validateToken(jwtToken)
                .map(claims -> {
                    String role = claims.get("role", String.class);
                    if (role == null || role.trim().isEmpty()) {
                        throw new InvalidJwtTokenException(ERROR_ROLE_NOT_FOUND);
                    }
                    return role;
                });
    }

    public Mono<Void> validateAdvisorOrAdminAccess(String jwtToken) {
        return validateTokenAndAnyRole(jwtToken, RoleId.ADMIN.name(), RoleId.ADVISOR.name());
    }
}
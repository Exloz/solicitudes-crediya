package co.com.bancolombia.consumer.service;

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

    private static final String ROLE_ADMIN = "Admin";
    private static final String ROLE_MANAGER = "Manager";
    private static final String ROLE_ADVISOR = "Advisor";
    private static final String ROLE_USER = "USER";
    private static final String ERROR_PERMISSION_PROFILE = "User does not have permission to access this profile";
    private static final String ERROR_PERMISSION_LOAN = "User does not have permission to access this loan application";
    private static final String ERROR_ROLE_NOT_FOUND = "Role not found in JWT token";
    private static final String ERROR_USERID_NOT_FOUND = "User ID not found in JWT token";
    private static final String ERROR_REQUIRED_ROLE = "User does not have required role: %s";
    private static final String ERROR_REQUIRED_ANY_ROLE = "User does not have any of the required roles: %s";

    public Mono<Void> validateTokenAndRole(String jwtToken, String requiredRole) {
        return Mono.fromCallable(() -> {
                    log.debug("Validating JWT token and role: {}", requiredRole);

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
                    log.debug("Validating JWT token and any of roles: {}", String.join(", ", requiredRoles));

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

    public Mono<Void> validateAdminOrManagerAccess(String jwtToken) {
        return validateTokenAndAnyRole(jwtToken, ROLE_ADMIN, ROLE_MANAGER);
    }

    public Mono<Void> validateUserProfileAccess(String jwtToken, String requestedUserId) {
        return validateToken(jwtToken)
                .flatMap(claims -> {
                    Long tokenUserId = claims.get("userId", Long.class);
                    String userRole = claims.get("role", String.class);

                    if (ROLE_ADMIN.equals(userRole)) {
                        return Mono.empty();
                    }

                    if (tokenUserId != null && tokenUserId.toString().equals(requestedUserId)) {
                        return Mono.empty();
                    }

                    return Mono.error(new InsufficientPrivilegesException(
                        ERROR_PERMISSION_PROFILE));
                });
    }

    public Mono<Void> validateLoanApplicationAccess(String jwtToken, String clientId) {
        return validateToken(jwtToken)
                .flatMap(claims -> {
                    Long tokenUserId = claims.get("userId", Long.class);
                    String userRole = claims.get("role", String.class);

                    if (ROLE_ADVISOR.equals(userRole)) {
                        return Mono.empty();
                    }

                    if (tokenUserId != null && tokenUserId.toString().equals(clientId)) {
                        return Mono.empty();
                    }

                    return Mono.error(new InsufficientPrivilegesException(
                        ERROR_PERMISSION_LOAN));
                });
    }

    public Mono<Void> validateCustomerAccess(String jwtToken) {
        return validateTokenAndRole(jwtToken, ROLE_USER);
    }

    public Mono<Void> validateAdminAccess(String jwtToken) {
        return validateTokenAndRole(jwtToken, ROLE_ADMIN);
    }

    public Mono<Void> validateAdvisorOrAdminAccess(String jwtToken) {
        return validateTokenAndAnyRole(jwtToken, ROLE_ADVISOR, ROLE_ADMIN);
    }
}
package co.com.bancolombia.consumer.service;

import co.com.bancolombia.model.exception.security.InsufficientPrivilegesException;
import co.com.bancolombia.model.exception.security.InvalidJwtTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.security.Key;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private JwtService jwtService;

    private AuthorizationService authorizationService;
    private String validToken;
    private String invalidToken = "invalid.jwt.token";

    @BeforeEach
    void setUp() {
        authorizationService = new AuthorizationService(jwtService);

        // Create a mock valid JWT token
        Key key = io.jsonwebtoken.security.Keys.secretKeyFor(SignatureAlgorithm.HS256);
        validToken = Jwts.builder()
                .setSubject("test-user")
                .claim("role", "Advisor")
                .claim("userId", 123L)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }

    @Test
    void validateTokenAndRole_success() {
        // Arrange
        Claims mockClaims = Jwts.claims().subject("test-user").build();
        mockClaims.put("role", "Advisor");
        mockClaims.put("userId", 123L);

        when(jwtService.validateToken(validToken)).thenReturn(mockClaims);
        when(jwtService.hasRequiredRole(mockClaims, "Advisor")).thenReturn(true);

        // Act & Assert
        StepVerifier.create(authorizationService.validateTokenAndRole(validToken, "Advisor"))
                .verifyComplete();
    }

    @Test
    void validateTokenAndRole_insufficientPrivileges() {
        // Arrange
        Claims mockClaims = Jwts.claims().subject("test-user").build();
        mockClaims.put("role", "User");
        mockClaims.put("userId", 123L);

        when(jwtService.validateToken(validToken)).thenReturn(mockClaims);
        when(jwtService.hasRequiredRole(mockClaims, "Advisor")).thenReturn(false);

        // Act & Assert
        StepVerifier.create(authorizationService.validateTokenAndRole(validToken, "Advisor"))
                .expectError(InsufficientPrivilegesException.class)
                .verify();
    }

    @Test
    void validateTokenAndRole_invalidToken() {
        // Arrange
        when(jwtService.validateToken(invalidToken))
                .thenThrow(new InvalidJwtTokenException("Invalid JWT token"));

        // Act & Assert
        StepVerifier.create(authorizationService.validateTokenAndRole(invalidToken, "Advisor"))
                .expectError(InvalidJwtTokenException.class)
                .verify();
    }

    @Test
    void validateTokenAndAnyRole_success() {
        // Arrange
        Claims mockClaims = Jwts.claims().subject("test-user").build();
        mockClaims.put("role", "Advisor");
        mockClaims.put("userId", 123L);

        when(jwtService.validateToken(validToken)).thenReturn(mockClaims);
        when(jwtService.hasRequiredRole(mockClaims, "Advisor", "Admin")).thenReturn(true);

        // Act & Assert
        StepVerifier.create(authorizationService.validateTokenAndAnyRole(validToken, "Advisor", "Admin"))
                .verifyComplete();
    }

    @Test
    void validateTokenAndAnyRole_noMatchingRole() {
        // Arrange
        Claims mockClaims = Jwts.claims().subject("test-user").build();
        mockClaims.put("role", "User");
        mockClaims.put("userId", 123L);

        when(jwtService.validateToken(validToken)).thenReturn(mockClaims);
        when(jwtService.hasRequiredRole(mockClaims, "Advisor", "Admin")).thenReturn(false);

        // Act & Assert
        StepVerifier.create(authorizationService.validateTokenAndAnyRole(validToken, "Advisor", "Admin"))
                .expectError(InsufficientPrivilegesException.class)
                .verify();
    }

    @Test
    void validateToken_success() {
        // Arrange
        Claims mockClaims = Jwts.claims().subject("test-user").build();
        mockClaims.put("role", "Advisor");

        when(jwtService.validateToken(validToken)).thenReturn(mockClaims);

        // Act & Assert
        StepVerifier.create(authorizationService.validateToken(validToken))
                .expectNext(mockClaims)
                .verifyComplete();
    }

    @Test
    void extractUserId_success() {
        // Arrange
        Claims mockClaims = Jwts.claims().subject("test-user").build();
        mockClaims.put("userId", 123L);

        when(jwtService.validateToken(validToken)).thenReturn(mockClaims);
        when(jwtService.getUserIdFromToken(mockClaims)).thenReturn(123L);

        // Act & Assert
        StepVerifier.create(authorizationService.extractUserId(validToken))
                .expectNext(123L)
                .verifyComplete();
    }

    @Test
    void extractUserId_nullUserId() {
        // Arrange
        Claims mockClaims = Jwts.claims().subject("test-user").build();

        when(jwtService.validateToken(validToken)).thenReturn(mockClaims);
        when(jwtService.getUserIdFromToken(mockClaims)).thenReturn(null);

        // Act & Assert
        StepVerifier.create(authorizationService.extractUserId(validToken))
                .expectError(InvalidJwtTokenException.class)
                .verify();
    }

    @Test
    void extractUserRole_success() {
        // Arrange
        Claims mockClaims = Jwts.claims().subject("test-user").build();
        mockClaims.put("role", "Advisor");

        when(jwtService.validateToken(validToken)).thenReturn(mockClaims);

        // Act & Assert
        StepVerifier.create(authorizationService.extractUserRole(validToken))
                .expectNext("Advisor")
                .verifyComplete();
    }

    @Test
    void extractUserRole_nullRole() {
        // Arrange
        Claims mockClaims = Jwts.claims().subject("test-user").build();

        when(jwtService.validateToken(validToken)).thenReturn(mockClaims);

        // Act & Assert
        StepVerifier.create(authorizationService.extractUserRole(validToken))
                .expectError(InvalidJwtTokenException.class)
                .verify();
    }
}
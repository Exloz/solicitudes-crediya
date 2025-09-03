package co.com.bancolombia.consumer.service;

import co.com.bancolombia.model.exception.security.ExpiredJwtTokenException;
import co.com.bancolombia.model.exception.security.InvalidJwtTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final PublicKey publicKey;

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new InvalidJwtTokenException("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtTokenException("JWT token has expired");
        } catch (MalformedJwtException e) {
            throw new InvalidJwtTokenException("Invalid JWT token format");
        } catch (UnsupportedJwtException e) {
            throw new InvalidJwtTokenException("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            throw new InvalidJwtTokenException("JWT token is null or empty");
        }
    }

    public boolean hasRequiredRole(Claims claims, String... requiredRoles) {
        String userRole = claims.get("role", String.class);
        return Arrays.asList(requiredRoles).contains(userRole);
    }

    public Long getUserIdFromToken(Claims claims) {
        return claims.get("userId", Long.class);
    }

    public boolean validateUserIdMatch(Claims claims, String requestedUserId) {
        Long tokenUserId = getUserIdFromToken(claims);
        return tokenUserId != null && tokenUserId.toString().equals(requestedUserId);
    }
}
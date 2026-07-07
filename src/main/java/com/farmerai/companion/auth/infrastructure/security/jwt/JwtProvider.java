package com.farmerai.companion.auth.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * Encapsulates all JWT operations: generation, validation, and claim extraction.
 *
 * <p>Uses HMAC-SHA256 (HS256) signing. Configuration is externalized via
 * {@code application.yml} properties.</p>
 */
@Component
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    private final SecretKey signingKey;
    private final long accessTokenExpirationMs;
    private final String issuer;

    public JwtProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token.expiration-ms}") long accessTokenExpirationMs,
            @Value("${app.jwt.issuer}") String issuer) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.issuer = issuer;
    }

    /**
     * Generates a JWT access token with standard claims.
     *
     * @param userId the user's UUID (becomes 'sub' claim)
     * @param email  the user's email
     * @param role   the user's role name
     * @return signed JWT string
     */
    public String generateAccessToken(UUID userId, String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Validates a JWT token's signature, expiration, and issuer.
     *
     * @param token the JWT string to validate
     * @return true if the token is valid
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaimsFromToken(token);
            // Verify issuer
            if (!issuer.equals(claims.getIssuer())) {
                log.warn("JWT issuer mismatch. Expected: {}, Got: {}", issuer, claims.getIssuer());
                return false;
            }
            return true;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extracts the user ID (subject) from the JWT.
     */
    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(parseClaimsFromToken(token).getSubject());
    }

    /**
     * Extracts the email claim from the JWT.
     */
    public String getEmailFromToken(String token) {
        return parseClaimsFromToken(token).get("email", String.class);
    }

    /**
     * Extracts the role claim from the JWT.
     */
    public String getRoleFromToken(String token) {
        return parseClaimsFromToken(token).get("role", String.class);
    }

    /**
     * Returns the access token expiration in seconds (for API responses).
     */
    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationMs / 1000;
    }

    // ======================== Internal ========================

    private Claims parseClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

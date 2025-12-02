package com.ybritto.teamtempo.backend.authentication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        try {
            String username = extractClaim(token, Claims::getSubject);
            logger.debug("Extracted username from token: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Failed to extract username from token", e);
            throw e;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            logger.error("Failed to extract claim from token", e);
            throw e;
        }
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        logger.debug("Generating JWT token for user: {}", userDetails.getUsername());
        String token = buildToken(extraClaims, userDetails, jwtExpiration);
        logger.info("JWT token generated successfully for user: {}", userDetails.getUsername());
        return token;
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
            
            if (!isValid) {
                if (!username.equals(userDetails.getUsername())) {
                    logger.warn("Token validation failed: username mismatch. Token username: {}, Expected: {}", 
                            username, userDetails.getUsername());
                } else {
                    logger.warn("Token validation failed: token expired for user: {}", username);
                }
            } else {
                logger.debug("Token validation successful for user: {}", username);
            }
            
            return isValid;
        } catch (Exception e) {
            logger.error("Error validating token for user: {}", userDetails.getUsername(), e);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            boolean expired = expiration.before(new Date());
            if (expired) {
                logger.debug("Token expired. Expiration date: {}", expiration);
            }
            return expired;
        } catch (Exception e) {
            logger.error("Error checking token expiration", e);
            throw e;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            logger.error("Failed to extract claims from token", e);
            throw e;
        }
    }

    private SecretKey getSignInKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            logger.error("Failed to generate signing key from secret", e);
            throw e;
        }
    }

}

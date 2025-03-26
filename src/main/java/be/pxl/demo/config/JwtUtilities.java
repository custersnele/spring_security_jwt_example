package be.pxl.demo.config;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class JwtUtilities {
    private static final Logger LOGGER = LogManager.getLogger(JwtUtilities.class);

    @Value("${jwt.jwtExpirationTime}")
    private Long jwtExpirationTime;
    
    private final KeyPair keyPair = Jwts.SIG.RS256.keyPair().build();
    private final Map<String, Claims> claimsCache = new ConcurrentHashMap<>();

    private PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    private PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Claims extractAllClaims(String token) {
        return claimsCache.computeIfAbsent(token, t -> 
            Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(t)
                .getPayload()
        );
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractUsername(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public String generateToken(String email, List<String> roles) {
        return Jwts.builder()
                .subject(email)
                .claim("role", roles)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(Date.from(Instant.now().plus(jwtExpirationTime, ChronoUnit.MILLIS)))
                .signWith(getPrivateKey())
                .compact();
    }

    public String generateRefreshToken(String email, String jti, Instant expirationDate) {
        return Jwts.builder()
                .subject(email)
                .id(jti)
                .issuedAt(new Date())
                .expiration(Date.from(expirationDate)) // long-lived
                .signWith(getPrivateKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getPublicKey()).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            LOGGER.info("Invalid JWT token.", e);
            throw e;
        } catch (ExpiredJwtException e) {
            LOGGER.info("Expired JWT token.", e);
            throw e;
        } catch (UnsupportedJwtException e) {
            LOGGER.info("Unsupported JWT token.", e);
            throw e;
        } catch (IllegalArgumentException e) {
            LOGGER.info("JWT token compact of handler are invalid.", e);
            throw e;
        }
    }

    public UUID extractTokenId(String token) {
        return UUID.fromString(extractClaim(token, Claims::getId)); // ID == jti
    }
    
    public String getToken(HttpServletRequest httpServletRequest) {
        final String bearerToken = httpServletRequest.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

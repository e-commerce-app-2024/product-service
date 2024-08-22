package com.ecommerce.app.security;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Date;

@Log4j2
@Component
public class JwtTokenUtil {

    private static String TOKEN_SECRET_KEY;
    private static Long ACCESS_TOKEN_VALIDITY;
    private static String ISSUER;
    private static String SIGNATURE_ALGORITHM;

    public JwtTokenUtil(@Value("${secret.key}") String secretKey,
                        @Value("${secret.issuer}") String issuer,
                        @Value("${secret.algorithm}") String algorithm,
                        @Value("${access.token.expiration}") Long accessValidity) {

        Assert.notNull(accessValidity, "accessValidity must not be null");
        Assert.hasText(secretKey, "secretKey must not be null or empty");
        Assert.hasText(algorithm, "algorithm must not be null or empty");
        Assert.hasText(issuer, "issuer must not be null or empty");

        TOKEN_SECRET_KEY = secretKey;
        ACCESS_TOKEN_VALIDITY = accessValidity;
        ISSUER = issuer;
        SIGNATURE_ALGORITHM = algorithm;
    }

    public static String generateToken(final String username, final String tokenId) {
        return Jwts.builder()
                .setId(tokenId)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setIssuer(ISSUER)
                .setExpiration(getTokenExpirationDate())
                .claim("created", Calendar.getInstance().getTime())
                .signWith(SignatureAlgorithm.valueOf(SIGNATURE_ALGORITHM), TOKEN_SECRET_KEY).compact();
    }


    private static Date getTokenExpirationDate() {
        return new Date(System.currentTimeMillis() + (ACCESS_TOKEN_VALIDITY * 1000));
    }

    public String getUserNameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getTokenIdFromToken(String token) {
        return getClaims(token).getId();
    }

    public boolean isTokenValid(String token, String userName) {
        boolean tokenExpired = isTokenExpired(token);
        log.info("isTokenExpired : {}", tokenExpired);
        String username = getUserNameFromToken(token);
        log.info("username from token : {}", username);
        boolean isUserNameEqual = username.equalsIgnoreCase(userName);
        boolean isTokenValid = isUserNameEqual && !tokenExpired;
        log.info("isTokenValid : {}", isTokenValid);
        return isTokenValid;
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(TOKEN_SECRET_KEY).parseClaimsJws(token).getBody();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(TOKEN_SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.info("Invalid JWT Signature");
        } catch (MalformedJwtException ex) {
            log.info("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.info("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.info("Unsupported JWT exception");
        } catch (IllegalArgumentException ex) {
            log.info("Jwt claims string is empty");
        }
        return false;
    }
}

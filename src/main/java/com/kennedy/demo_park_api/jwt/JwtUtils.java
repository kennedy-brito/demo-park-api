package com.kennedy.demo_park_api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class JwtUtils {

    public static final String JWT_BEARER = "Bearer ";
    public static final String JWT_AUTHORIZATION = "Authorization ";
    public static final String SECRET_KEY = "0123456789-0123456789-0123456789";
    public static final long EXPIRE_DAYS = 0L;
    public static final long EXPIRE_HOURS = 0L;
    public static final long EXPIRE_MINUTES = 2L;

    private JwtUtils(){}

    private static SecretKey generateKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    private static Date toExpireDate(Date start){
        LocalDateTime dateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime end = dateTime
                .plusDays(EXPIRE_DAYS)
                .plusHours(EXPIRE_HOURS)
                .plusMinutes(EXPIRE_MINUTES);

        return Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static   JwtToken createToken(String username, String role){
        Date issuedAt = new Date();

        Date limit = toExpireDate(issuedAt);

        String token = Jwts.builder()
                .header().add("typ", "jwt")
                .and()
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(limit)
                .signWith(generateKey())
                .claims().add("role", role)
                .and()
                .compact();

        return new JwtToken(token);
    }

    private static Claims getClaimsFromToken(String token){
        try{
            return Jwts.parser()
                    .verifyWith(generateKey()).build()
                    .parseSignedClaims(refactorToken(token)).getPayload();
        }catch (JwtException e){
            log.error(String.format("Invalid Token %s", e.getMessage()));
        }
        return null;
    }

    private static String refactorToken(String token){
        if( token.contains(JWT_BEARER)){
            return token.substring(JWT_BEARER.length());
        }
        return token;
    }

    public static String getUsernameFromToken(String token){
        return getClaimsFromToken(token).getSubject();
    }

    public static boolean isTokenValid(String token){
        try{
            Jwts.parser()
                .verifyWith(generateKey()).build()
                .parseSignedClaims(refactorToken(token));

            return true;
        }catch (JwtException e){
            log.error(String.format("Invalid Token %s", e.getMessage()));
        }
        return false;
    }
}
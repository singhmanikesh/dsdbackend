package com.onesolutions.dsd.Utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class Jwtutil {
    @Value("${jwt.secret.key}")
    private  String SECRET_KEY;
    @Value("${jwt.access.expiration}")
    private long ACCESS_TOKEN_VALIDITY;
    @Value("${jwt.refresh.expiration}")
    private long REFRESH_TOKEN_VALIDITY;

    public String generateAccessToken(String userdetails) {
        // claims == piece of token embedded in a jwt token
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userdetails , ACCESS_TOKEN_VALIDITY);
    }
    // refersh token
    public String generateRefreshToken(String userdetails){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userdetails , REFRESH_TOKEN_VALIDITY);
    }

    private String createToken(Map<String, Object> claims, String username , long validity) {
        return Jwts.builder()
                .setClaims(claims)  // payload data
                .setSubject(username)
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                // setting expiration time for 10 hours
                .setExpiration(new java.util.Date(System.currentTimeMillis() + validity)) // 10 hours
                .signWith(getSecretKey())
                // generates the final jwt string
                .compact();
    }

    // extraction of payload jwt

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);     // Claims is an interface so the subject is being retirived via getsubject emthod
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaim(token);
        return claimsResolver.apply(claims);

    }

    private Claims extractAllClaim(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey()) // secret key for verifying signature
                .build()
                .parseClaimsJws(token)          // parses the signed JWT
                .getBody();                     // returns Claims
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

    }

    public Boolean isRefreshTokenValid(String token){
        return !isTokenExpired(token);
    }

}
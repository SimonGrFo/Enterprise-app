package com.example.enterprise_app.service;

import com.example.enterprise_app.model.User;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.MalformedJwtException;

import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    // Use a secure generated key for HS512
    private Key jwtSecretKey;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;



    @PostConstruct
    public void init() {
        // Generate a secure 512-bit key for HS512
        this.jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserNameFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();  // Return the username (subject)
    }

    public String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)  // Use the secure generated key
                .compact();
    }
}

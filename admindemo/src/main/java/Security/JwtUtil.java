package com.example.admindemo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtUtil.class);


    private static final String SECRET =
            "mysecretkeymysecretkeymysecretkey123456"; // must be >= 32 chars


    private final Key key =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));


    // 🔐 Generate Token
    public String generateToken(String username) {

        logger.info("[JwtUtil] STEP 8 -> Generating JWT token");
        logger.info("[JwtUtil] Username for token: {}", username);

        String token =
                Jwts.builder()
                        .setSubject(username)
                        .setIssuedAt(new Date())
                        .setExpiration(
                                new Date(System.currentTimeMillis() + 1000 * 60 *60)
                        )
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();


        logger.info("[JwtUtil] JWT generated successfully");

        return token;
    }


    // 🔍 Extract Username
    public String extractUsername(String token) {

        logger.info("[JwtUtil] Extracting username from JWT");

        String username =
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();


        logger.info("[JwtUtil] Username extracted from JWT: {}", username);

        return username;
    }


    // ✔ Validate Token
    public boolean validateToken(String token) {

        logger.info("[JwtUtil] Validating JWT token");

        try {

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);


            logger.info("[JwtUtil] JWT validation successful");

            return true;


        } catch (ExpiredJwtException e) {

            logger.warn("[JwtUtil] JWT expired");

            return false;


        } catch (SignatureException e) {

            logger.warn("[JwtUtil] Invalid JWT signature");

            return false;


        } catch (Exception e) {

            logger.warn("[JwtUtil] Invalid JWT token");

            return false;
        }
    }
}
package com.library.library_management.service.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;



@Service
public class JwtService {


    private final SecretKey key;

    private final long expiration;



    public JwtService(

            @Value("${security.jwt.secret}")
            String secret,

            @Value("${security.jwt.expiration}")
            long expiration

    ){

        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expiration = expiration;

    }



    public String generateToken(UserDetails userDetails){


        return Jwts.builder()

                .subject(userDetails.getUsername())

                .issuedAt(new Date())

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )

                .signWith(key)

                .compact();

    }




    public String extractUsername(String token){

        return extractClaims(token)
                .getSubject();

    }




    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ){

        String username =
                extractUsername(token);


        return username.equals(
                userDetails.getUsername()
        )
                &&
                !extractClaims(token)
                        .getExpiration()
                        .before(
                                new Date()
                        );

    }




    private Claims extractClaims(String token){


        return Jwts.parser()

                .verifyWith(key)

                .build()

                .parseSignedClaims(token)

                .getPayload();

    }


}
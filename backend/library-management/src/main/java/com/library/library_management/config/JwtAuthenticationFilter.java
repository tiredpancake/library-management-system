package com.library.library_management.config;

import com.library.library_management.service.security.AppUserDetailsService;
import com.library.library_management.service.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;

    private final AppUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        final String authHeader = request.getHeader("Authorization");


        System.out.println("REQUEST = " + request.getRequestURI());
        System.out.println("AUTH HEADER = " + authHeader);


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }


        try {

            String jwt = authHeader.substring(7);


            System.out.println("JWT = " + jwt);


            String username = jwtService.extractUsername(jwt);


            System.out.println("USERNAME = " + username);


            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {


                UserDetails userDetails = userDetailsService.loadUserByUsername(username);


                boolean valid = jwtService.isTokenValid(jwt, userDetails);


                System.out.println("TOKEN VALID = " + valid);


                if (valid) {


                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());


                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


                    SecurityContextHolder.getContext().setAuthentication(authToken);


                    System.out.println("AUTHENTICATION SET");
                }
            }


        } catch (Exception e) {

            System.out.println("JWT ERROR = " + e.getMessage());

        }


        filterChain.doFilter(request, response);

    }
}
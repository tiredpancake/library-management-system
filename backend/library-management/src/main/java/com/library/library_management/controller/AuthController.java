package com.library.library_management.controller;


import com.library.library_management.dto.auth.LoginResponse;
import com.library.library_management.service.security.JwtService;

import lombok.RequiredArgsConstructor;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {



    private final AuthenticationManager authenticationManager;


    private final JwtService jwtService;




    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request
    ){


        Authentication authentication =
                authenticationManager.authenticate(

                        new UsernamePasswordAuthenticationToken(

                                request.username(),

                                request.password()

                        )

                );



        String token =
                jwtService.generateToken(
                        (org.springframework.security.core.userdetails.UserDetails)
                                authentication.getPrincipal()
                );



        return new LoginResponse(token);

    }





    @GetMapping("/current-user")
    public String getCurrentUser(){

        return com.library.library_management.security.SecurityUtils
                .getCurrentUsername();

    }





    public record LoginRequest(

            String username,

            String password

    ){}


}
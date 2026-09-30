package com.library.library_management.controller;


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



    @PostMapping("/login")
    public String login(
            @RequestBody LoginRequest request
    ){

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );


        return "Login successful";

    }



    @GetMapping("/current-user")
    public String getCurrentUser(){

        return com.library.library_management.security.SecurityUtils.getCurrentUsername();

    }



    public record LoginRequest(
            String username,
            String password
    ){}

}
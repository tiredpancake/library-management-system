package com.library.library_management.controller;

import com.library.library_management.dto.auth.LoginResponse;
import com.library.library_management.security.SecurityUtils;
import com.library.library_management.service.EventLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final EventLogger eventLogger;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        String username = authentication.getName();
        eventLogger.info("AUTH_LOGIN_SUCCESS", username, "authentication=basic");
        return new LoginResponse(username);
    }

    @GetMapping("/current-user")
    public String getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        eventLogger.info("AUTH_CURRENT_USER", username, "result=success");
        return username;
    }

    public record LoginRequest(String username, String password) {
    }
}

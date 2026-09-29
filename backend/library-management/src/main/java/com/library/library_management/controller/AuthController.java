package com.library.library_management.controller;

import com.library.library_management.security.SecurityUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @GetMapping("/current-user")
    public String getCurrentUser(){

        return SecurityUtils.getCurrentUsername();

    }

}
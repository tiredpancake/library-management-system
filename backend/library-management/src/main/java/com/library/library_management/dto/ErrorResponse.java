package com.library.library_management.dto;


import java.time.LocalDateTime;


public record ErrorResponse(

        String message,
        Integer status,
        LocalDateTime timestamp

) {}
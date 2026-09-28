package com.library.library_management.exception;


import com.library.library_management.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(
            ResourceNotFoundException ex
    ) {


        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        new ErrorResponse(
                                ex.getMessage(),
                                404,
                                LocalDateTime.now()
                        )
                );

    }



    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicate(
            DuplicateResourceException ex
    ) {


        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ErrorResponse(
                                ex.getMessage(),
                                409,
                                LocalDateTime.now()
                        )
                );

    }

}
package com.library.library_management.exception;


import com.library.library_management.dto.ErrorResponse;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.orm.ObjectOptimisticLockingFailureException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.LocalDateTime;


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(null, ex.getMessage(), 404, LocalDateTime.now()));

    }


    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ErrorResponse(
                                ex.getField(),
                                ex.getMessage(),
                                409,
                                LocalDateTime.now()
                        )
                );

    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {


        var error = ex.getBindingResult().getFieldErrors().get(0);


        return ResponseEntity.badRequest().body(

                new ErrorResponse(

                        error.getField(),

                        error.getDefaultMessage(),

                        400,

                        LocalDateTime.now()

                )

        );

    }


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(

                new ErrorResponse(

                        null,

                        ex.getMessage(),

                        400,

                        LocalDateTime.now()

                )

        );

    }


    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock() {


        return ResponseEntity.status(HttpStatus.CONFLICT).body(

                new ErrorResponse(

                        null,

                        "Book was modified by another user. Please try again.",

                        409,

                        LocalDateTime.now()

                )

        );


    }


}
package com.library.library_management.exception;

import com.library.library_management.dto.ErrorResponse;
import com.library.library_management.security.SecurityUtils;
import com.library.library_management.service.EventLogger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final EventLogger eventLogger;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        eventLogger.warn("REQUEST_NOT_FOUND", actor(request), requestSummary(request, ex));
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(null, ex.getMessage(), 404, LocalDateTime.now()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex,
            HttpServletRequest request
    ) {
        eventLogger.warn("REQUEST_CONFLICT", actor(request), requestSummary(request, ex));
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getField(), ex.getMessage(), 409, LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        var error = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String field = error == null ? null : error.getField();
        String message = error == null ? "Invalid request" : error.getDefaultMessage();

        eventLogger.warn("REQUEST_VALIDATION_FAILED", actor(request),
                requestSummary(request, new IllegalArgumentException(message)));

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(field, message, 400, LocalDateTime.now()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException ex,
            HttpServletRequest request
    ) {
        eventLogger.warn("BUSINESS_OPERATION_FAILED", actor(request), requestSummary(request, ex));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(null, ex.getMessage(), 400, LocalDateTime.now()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        eventLogger.warn("AUTH_LOGIN_FAILED", actor(request),
                request.getMethod() + " " + request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(null, "Invalid username or password", 401, LocalDateTime.now()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(
            ObjectOptimisticLockingFailureException ex,
            HttpServletRequest request
    ) {
        eventLogger.warn("OPTIMISTIC_LOCK_CONFLICT", actor(request), requestSummary(request, ex));
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        null,
                        "Book was modified by another user. Please try again.",
                        409,
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) {
        eventLogger.error("INTERNAL_SERVER_ERROR", actor(request),
                requestSummary(request, ex), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        null,
                        "Internal server error. Please try again later.",
                        500,
                        LocalDateTime.now()
                ));
    }

    private String actor(HttpServletRequest request) {
        return SecurityUtils.getCurrentUsername();
    }

    private String requestSummary(HttpServletRequest request, Exception ex) {
        return request.getMethod() + " " + request.getRequestURI()
                + " exception=" + ex.getClass().getSimpleName();
    }
}

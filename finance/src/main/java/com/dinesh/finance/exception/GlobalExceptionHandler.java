package com.dinesh.finance.exception;



import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── VALIDATION ERRORS ────────────────────────────────
    // Triggered when @Valid fails on request body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return build(HttpStatus.BAD_REQUEST, "Validation Failed", message);
    }

    // ─── ACCESS DENIED ────────────────────────────────────
    // Triggered when role doesn't have permission
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "Access Denied",
                "You do not have permission to perform this action");
    }

    // ─── BAD CREDENTIALS ──────────────────────────────────
    // Triggered when wrong email or password on login
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Authentication Failed",
                "Invalid email or password");
    }

    // ─── DISABLED ACCOUNT ─────────────────────────────────
    // Triggered when deactivated user tries to login
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled(DisabledException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Account Disabled",
                "Your account has been deactivated. Contact admin.");
    }

    // ─── GENERAL RUNTIME ERRORS ───────────────────────────
    // Triggered by throw new RuntimeException(...) in services
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        return build(HttpStatus.BAD_REQUEST, "Request Failed", ex.getMessage());
    }

    // ─── CATCH ALL ────────────────────────────────────────
    // Any unexpected error lands here
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
           System.out.println(ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "Something went wrong. Please try again.");
    }

    // ─── HELPER ───────────────────────────────────────────
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        status.value(),
                        error,
                        message,
                        LocalDateTime.now()
                ));
    }
}
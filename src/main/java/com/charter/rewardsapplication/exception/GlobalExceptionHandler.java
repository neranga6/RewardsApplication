package com.charter.rewardsapplication.exception;

import com.charter.rewardsapplication.model.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(
                        LocalDateTime.now(),
                        404,
                        "Not Found",
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(
                        LocalDateTime.now(),
                        400,
                        "Bad Request",
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }
}
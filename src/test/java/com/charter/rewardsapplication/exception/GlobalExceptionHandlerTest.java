package com.charter.rewardsapplication.exception;

import com.charter.rewardsapplication.model.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_ShouldReturn404Response() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/rewards/999");

        ResourceNotFoundException ex = new ResourceNotFoundException("Customer not found: 999");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Customer not found: 999");
        assertThat(response.getBody().path()).isEqualTo("/api/rewards/999");
    }

    @Test
    void handleBadRequest_ShouldReturn400Response() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/rewards/transactions");

        IllegalArgumentException ex = new IllegalArgumentException("Amount cannot be negative");

        ResponseEntity<ApiErrorResponse> response = handler.handleBadRequest(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Amount cannot be negative");
        assertThat(response.getBody().path()).isEqualTo("/api/rewards/transactions");
    }
}
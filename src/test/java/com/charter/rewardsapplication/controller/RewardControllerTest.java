package com.charter.rewardsapplication.controller;

import com.charter.rewardsapplication.exception.GlobalExceptionHandler;
import com.charter.rewardsapplication.exception.ResourceNotFoundException;
import com.charter.rewardsapplication.model.CreateTransactionRequest;
import com.charter.rewardsapplication.model.CustomerRewardsSummary;
import com.charter.rewardsapplication.model.MonthlyPoints;
import com.charter.rewardsapplication.model.PurchaseTransaction;
import com.charter.rewardsapplication.service.RewardCalculationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardController.class)
@Import(GlobalExceptionHandler.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RewardCalculationService service;

    @Test
    void getAll_ShouldReturn200AndRewardsList() throws Exception {
        CustomerRewardsSummary summary = new CustomerRewardsSummary(
                101L,
                "John",
                List.of(new MonthlyPoints("January", 115)),
                115
        );

        when(service.getAllRewards()).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customer_id").value(101))
                .andExpect(jsonPath("$[0].customer_name").value("John"))
                .andExpect(jsonPath("$[0].total_points").value(115))
                .andExpect(jsonPath("$[0].monthly_points[0].month").value("January"))
                .andExpect(jsonPath("$[0].monthly_points[0].points").value(115));
    }

    @Test
    void getByCustomer_ShouldReturn200AndRewardSummary() throws Exception {
        CustomerRewardsSummary summary = new CustomerRewardsSummary(
                101L,
                "John",
                List.of(new MonthlyPoints("January", 115)),
                115
        );

        when(service.getRewardsByCustomer(101L)).thenReturn(summary);

        mockMvc.perform(get("/api/rewards/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer_id").value(101))
                .andExpect(jsonPath("$.customer_name").value("John"))
                .andExpect(jsonPath("$.total_points").value(115));
    }

    @Test
    void getByCustomer_ShouldReturn404_WhenCustomerNotFound() throws Exception {
        when(service.getRewardsByCustomer(999L))
                .thenThrow(new ResourceNotFoundException("Customer not found: 999"));

        mockMvc.perform(get("/api/rewards/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Customer not found: 999"))
                .andExpect(jsonPath("$.path").value("/api/rewards/999"));
    }

    @Test
    void create_ShouldReturn201AndSavedTransaction() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                10L,
                200L,
                "Alice",
                120.0,
                LocalDate.of(2026, 3, 26)
        );

        PurchaseTransaction saved = PurchaseTransaction.builder()
                .id(10L)
                .customerId(200L)
                .customerName("Alice")
                .amount(120.0)
                .transactionDate(LocalDate.of(2026, 3, 26))
                .build();

        when(service.createTransaction(any(CreateTransactionRequest.class))).thenReturn(saved);

        mockMvc.perform(post("/api/rewards/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.customerId").value(200))
                .andExpect(jsonPath("$.customerName").value("Alice"))
                .andExpect(jsonPath("$.amount").value(120.0));
    }

    @Test
    void create_ShouldReturn400_WhenAmountIsNegative() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                11L,
                201L,
                "Bob",
                -10.0,
                LocalDate.of(2026, 3, 26)
        );

        when(service.createTransaction(any(CreateTransactionRequest.class)))
                .thenThrow(new IllegalArgumentException("Amount cannot be negative"));

        mockMvc.perform(post("/api/rewards/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Amount cannot be negative"))
                .andExpect(jsonPath("$.path").value("/api/rewards/transactions"));
    }
}

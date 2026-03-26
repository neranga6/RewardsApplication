package com.charter.rewardsapplication.controller;


import com.charter.rewardsapplication.model.CreateTransactionRequest;
import com.charter.rewardsapplication.model.CustomerRewardsSummary;
import com.charter.rewardsapplication.model.PurchaseTransaction;
import com.charter.rewardsapplication.service.RewardCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for reward-related endpoints.
 */
@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardCalculationService service;

    /**
     * Retrieves reward summaries for all customers.
     *
     * @return list of all customer reward summaries
     */
    @Operation(summary = "Get all customer rewards")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved rewards")
    @GetMapping
    public ResponseEntity<List<CustomerRewardsSummary>> getAll() {
        return ResponseEntity.ok(service.getAllRewards());
    }

    /**
     * Retrieves reward summary for a specific customer.
     *
     * @param customerId customer id
     * @return reward summary for the given customer
     */
    @Operation(summary = "Get rewards by customer ID")
    @ApiResponse(responseCode = "200", description = "Customer rewards found")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerRewardsSummary> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getRewardsByCustomer(customerId));
    }

    /**
     * Creates a new purchase transaction.
     *
     * @param req request payload
     * @return created transaction
     */
    @Operation(summary = "Create a new transaction")
    @ApiResponse(responseCode = "201", description = "Transaction created")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @PostMapping("/transactions")
    public ResponseEntity<PurchaseTransaction> create(@RequestBody CreateTransactionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createTransaction(req));
    }
}
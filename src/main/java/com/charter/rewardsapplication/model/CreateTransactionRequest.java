package com.charter.rewardsapplication.model;

import java.time.LocalDate;

public record CreateTransactionRequest(
        Long customerId,
        String customerName,
        Double amount,
        LocalDate transactionDate
) {}
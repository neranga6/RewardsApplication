package com.charter.rewardsapplication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record CreateTransactionRequest(

        @JsonProperty("id")
        Long id,

        @JsonProperty("customer_id")
        Long customerId,

        @JsonProperty("customer_name")
        String customerName,

        @JsonProperty("amount")
        Double amount,

        @JsonProperty("transaction_date")
        LocalDate transactionDate
) {}
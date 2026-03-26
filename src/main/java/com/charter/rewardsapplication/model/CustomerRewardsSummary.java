package com.charter.rewardsapplication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CustomerRewardsSummary(

        @JsonProperty("customer_id")
        Long customerId,

        @JsonProperty("customer_name")
        String customerName,

        @JsonProperty("monthly_points")
        List<MonthlyPoints> monthlyPoints,

        @JsonProperty("total_points")
        int totalPoints
) {}
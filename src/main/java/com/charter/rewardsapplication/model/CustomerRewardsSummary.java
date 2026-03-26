package com.charter.rewardsapplication.model;

import java.util.List;

public record CustomerRewardsSummary(
        Long customerId,
        String customerName,
        List<MonthlyPoints> monthlyPoints,
        int totalPoints
) {}
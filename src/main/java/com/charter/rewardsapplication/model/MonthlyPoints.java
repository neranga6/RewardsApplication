package com.charter.rewardsapplication.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MonthlyPoints(
        @JsonProperty("month") String month,
        @JsonProperty("points") int points
) {}